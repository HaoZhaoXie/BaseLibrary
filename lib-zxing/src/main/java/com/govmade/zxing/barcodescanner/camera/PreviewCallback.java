package com.govmade.zxing.barcodescanner.camera;

import com.govmade.zxing.barcodescanner.SourceData;
/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
