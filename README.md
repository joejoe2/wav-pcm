# wav-pcm
v0.9-支援拖曳wav檔

v0.91-支援重複撥放 緩和在檔案較小的情形下的內存疊加(大檔案仍會發生一定程度的疊加)  jvm參數 -Xmx1024m  -XX:+UseSerialGC -XX:MaxHeapFreeRatio=0 -XX:MinHeapFreeRatio=0 -Xms128m -XX:InitiatingHeapOccupancyPercent=5
