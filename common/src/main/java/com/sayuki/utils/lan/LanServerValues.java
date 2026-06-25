package com.sayuki.utils.lan;

import net.minecraft.network.chat.Component;

public interface LanServerValues {
    // TunnelTypeを取得
    TunnelType getTunnelType();
    // TunnelTypeを設定
    void setTunnelType(TunnelType tunnelType);
    // TunnelTextを取得
    Component getTunnelText();
    // TunnelTextを設定
    void setTunnelText(Component tunnelText);
    // RawMotdを取得
    String getRawMotd();
    // RawMotdを設定
    void setRawMotd(String rawMotd);
}
