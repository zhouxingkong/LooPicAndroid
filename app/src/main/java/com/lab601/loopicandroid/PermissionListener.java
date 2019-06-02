package com.lab601.loopicandroid;

import java.util.List;

public interface PermissionListener {
    void granted();

    void denied(List<String> deniedList);
}
