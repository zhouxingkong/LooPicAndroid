package com.lab601.loopicandroid;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Utils {

    // 打乱列表实现方法2
    public static <T> void shuffle(List<T> list) {
        int size = list.size();
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            // 获取随机位置
            int randomPos = random.nextInt(size);

            // 当前元素与随机元素交换
            Collections.swap(list, i, randomPos);
        }
    }
}
