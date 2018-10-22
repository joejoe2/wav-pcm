# wav-pcm
update 2018 10 22

功能:讀取並分析音樂檔的訊號(波形、頻率)

支援檔案格式:wav、mp3

主要架構:

  1.由run.exe / run.jar呼叫testaudio.jar

  2.執行程式主體(gui->testaudio->swingcanvas->gui)

  3.測試記憶體占用 視情況重啟jvm


穩定版本: v1.0

JDK版本:1.8.0_181

library:mp3spi

更新方式:
         於本頁下載TestAudio.jar覆蓋原同名檔案即可(v0.94以下的版本要多下載lib資料夾!!!!!!!)
         
----------------------------------------------------------------------------------------------------------------------------------------

初次使用請至google drive下載完整版

完整檔案位置(v1.0):   https://drive.google.com/file/d/1E9n5cMKU0PY7gFEOlqqt_b4gLBloZIVP/view?usp=sharing

若要使用裝置本身的jre請點擊run.jar啟動(可刪除jre資料夾)

--------------------------------------------------------------------------------------------------------------------------------------
v0.1-成功讀取wav

v0.9-支援拖曳wav檔 修正sample長度無法剛好取平均的問題

v0.91-支援重複撥放 緩和在檔案較小的情形下的內存疊加(大檔案仍會發生一定程度的疊加)  jvm參數 -Xmx1024m  -XX:+UseSerialGC -XX:MaxHeapFreeRatio=0 -XX:MinHeapFreeRatio=0 -Xms128m -XX:InitiatingHeapOccupancyPercent=5

v0.92-適度修正大檔案的內存疊加 當回到選擇畫面時記憶體>324mb則重啟jvm 新增現正撥放文字、時間進度、終止按鈕  加入波形樣式選擇  修正終止後偶發持續撥放的問題

v0.94-支援mp3格式、下調記憶體限制至300mb   由於舊式java sound api無法讀取mp3且新式java fx可讀但無法進行frame level的操作 故改以jlayer把mp3轉成temp.wav再進行讀取(當程式結束會自動刪除)

v0.95-修正暫存檔的刪除流程 修正拖曳處理

v0.97-新增頻率分析功能

v0.98-新增檢查更新功能

v0.985-修正波形參數

v0.99-調整參數提升精細度

v0.995-新增自動下載更新功能

v1.0-正式版發佈
