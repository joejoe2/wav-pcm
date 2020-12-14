# wav-pcm

功能:讀取並分析音樂檔的訊號(波形、頻率)、提供遊戲模式(可自主編寫譜面)

支援檔案格式:wav、mp3

使用教學： https://youtu.be/i8-5MB8ArHs

使用方式：

  1.解壓縮檔案

  2.點擊run.exe / run.jar
  
  3.game mode目前還不好使用

初次使用請下載完整版　以下連結中assets展開的testaudio.rar

https://github.com/joejoe2/wav-pcm/releases/

windows可直接使用　其他系統請先安裝好java runtime environment

若要使用裝置本身的jre請點擊run.jar啟動(可刪除jre資料夾)
notice:this jre is only for windows


穩定版本: v1.08

JDK版本:1.8.0_181

----------------------------------------------------------------------------------------------------------------------------------------
更新方式:
         
         由主程式中的check update按鈕自動進行

--------------------------------------------------------------------------------------------------------------------------------------
v0.1-成功讀取wav

v0.9-支援拖曳wav檔 修正sample長度無法剛好取平均的問題

v0.91-支援重複撥放 緩和在檔案較小的情形下的內存疊加(大檔案仍會發生一定程度的疊加)  jvm參數 -Xmx1024m  -XX:+UseSerialGC -XX:MaxHeapFreeRatio=0 -XX:MinHeapFreeRatio=0 -Xms128m -XX:InitiatingHeapOccupancyPercent=5

v0.92-適度修正大檔案的內存疊加 當回到選擇畫面時記憶體>324mb則重啟jvm 新增現正撥放文字、時間進度、終止按鈕  加入波形樣式選擇  修正終止後偶發持續撥放的問題

v0.94-支援mp3格式、下調記憶體限制至300mb   由於舊式java sound api無法讀取mp3且新式java fx可讀但無法進行frame level的操作 故改以mp3spi把mp3轉成temp.wav再進行讀取(當程式結束會自動刪除)

v0.95-修正暫存檔的刪除流程 修正拖曳處理

v0.97-新增頻率分析功能

v0.98-新增檢查更新功能

v0.985-修正波形參數

v0.99-調整參數提升精細度

v0.995-新增自動下載更新功能

v1.0-正式版發佈

v1.03-新增時間條功能(測試中...)

v1.04-變更布景顏色、繪圖函數改為paintComponet、原本import com.sun.media.sound.FFT改為直接複製該.java檔至專案中防止java9以上的版本無法正確執行 

v1.05-修正計算錯誤 新增可選擇分析細節多寡(影響取平均數)   目前gamemode為empty

v1.051-刪除過快的模式(too many repaint may out of control)

v1.052-調整視窗大小、繪圖參數

v1.053-增加musiclist功能

v1.054-降低cpu使用率

v1.055-修正在檔案結尾的錯誤

v1.056-改進search policy  gamemode完成50%

v1.057-修正暫存檔刪除的bug  gamemode完成90%

v1.06-gamemode完成

v1.071-增加修復lib功能、fft改用jtransform、頻譜加入a-weighting修正

v1.072-頻譜改用b-weighting修正、顯示平滑化

v1.08-refactor program structure of visualizer part、分析計算時改用audio stream即時讀取(可減少部分memory使用)
