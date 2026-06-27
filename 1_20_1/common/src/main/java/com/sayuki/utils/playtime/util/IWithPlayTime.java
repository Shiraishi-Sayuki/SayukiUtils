/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime.util;

public interface IWithPlayTime {
    // プレイ時間を設定
    void setPlayTimeTicks(int playTimeTicks);
    // プレイ時間を取得
    int getPlayTimeTicks();
}
