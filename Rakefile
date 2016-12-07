
namespace :app do
    task :build do
        sh './gradlew build connectedCheck'
    end

    task :update_build_number do
        File.open('TheOne/AndroidManifest.xml', 'r+') do |f|
            manifest = f.read

            build = ENV['TRAVIS_BUILD_NUMBER'] || 0
            # Update version
            manifest = manifest.gsub(
                /android:versionCode=".*"/, "android:versionCode=\"#{build}\"")

            # Write back
            f.rewind
            f.truncate(0)
            f.write(manifest)
        end
    end
end

namespace :distribute do

    task :hockeyapp do
        release_note = `git log -1 --pretty=format:%s`

        sh 'curl https://rink.hockeyapp.net/api/2/apps/$HOCKEY_APP_ID/app_versions' \
            ' -F status="2"' \
            ' -F notify="0"' \
            " -F notes=\"#{release_note}\"" \
            ' -F notes_type="1"' \
            ' -F ipa="@$PWD/build/outputs/apk/android-client-debug.apk"' \
            ' -F teams=7836' \
            ' -F commit_sha=$TRAVIS_COMMIT' \
            ' -F repository_url=https://github.com/theone-dev/android-client' \
            ' -F build_server_url=https://magnum.travis-ci.com/theone-dev/android-client/builds/$TRAVIS_BUILD_ID' \
            ' -H "X-HockeyAppToken: $HOCKEY_APP_TOKEN"'
    end
end

task :test do
    # Test scheme
end

task :distribute do
    branch = ENV["TRAVIS_BRANCH"] || 'master'
    is_pr = ENV['TRAVIS_PULL_REQUEST'] != 'false'
    if branch != "master" or is_pr
        puts "Bypass distribution"
        exit
    end

    Rake::Task['app:update_build_number'].invoke
    Rake::Task['app:build'].invoke
    Rake::Task['distribute:hockeyapp'].invoke
end
