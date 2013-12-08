package com.adepthub.client.download;

import java.util.List;
import java.util.concurrent.Callable;

public interface DownloadProcess
    extends Callable<List<Result>>, ProgressTracking, Cancelable {}
