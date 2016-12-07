package com.theone.sns.component.location.poi;

import java.util.List;

public interface IPoiListener {

	void onResult(boolean result, List<LocalPoiInfo> poiInfoList);
}
