# wav-pcm
update 2018 10 16
主要架構:

  1.由run.exe / run.jar呼叫testaudio.jar

  2.執行程式主體(gui->testaudio->swingcanvas->gui)

  3.測試記憶體占用 視情況重啟jvm


v0.1-成功讀取wav

v0.9-支援拖曳wav檔 修正sample長度無法剛好取平均的問題

v0.91-支援重複撥放 緩和在檔案較小的情形下的內存疊加(大檔案仍會發生一定程度的疊加)  jvm參數 -Xmx1024m  -XX:+UseSerialGC -XX:MaxHeapFreeRatio=0 -XX:MinHeapFreeRatio=0 -Xms128m -XX:InitiatingHeapOccupancyPercent=5

0.92-適度修正大檔案的內存疊加 當回到選擇畫面時記憶體>300mb則重啟jvm
