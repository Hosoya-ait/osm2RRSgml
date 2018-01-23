OpenStreetMap(略称：OSM)の地図データをRoboCupRescue Simulation(略称：RRS)で使用できるデータ形式に変換するコンバーターである．

*[推奨環境]*

・OS : Ubuntu もしくは MacOS

・javaの入ったPC

*[使用方法]*

1.このページの右上にある「Clone or download」という緑色のボタンから「Download ZIP」を選択してzipファイルをダウンロードする．

2.ダウンロードした「osm2RRSgml-master.zip」ファイルを解凍する．

3.解凍した「osm2RRSgml-master」フォルダを任意の場所に移動させる．

4.「端末」や「ターミナル」などのコマンドラインを使って「osm2RRSgml-master」フォルダ内へ移動する．

5.コマンド 'java OsmToGmlConverter Sample1' を入力することで「osm2RRSgml」が起動する．

6.コマンドライン上に「owata」と表示されていればプログラムは終了している．

*[詳しい解説]*

「osm2RRSgml」フォルダ内にある「.class」で終わるファイルはすべてプログラムである．

「OSMs」フォルダには，サンプルとして札幌市と名古屋市から採取したOSMデータがそれぞれ「Sample1」「Sample2」として入っている．

「osm2RRSgml」によって処理され，作成されたRRSの地図データは「GMLs」フォルダ内に保存されている．

コマンドは「java OsmToGmlConverter [「.osm」を省いたOSMデータの名前]」と入力することで希望のOSMの地図データを変換できる．

つまり，「java OsmToGmlConverter Sample2」とすれば，OSMsフォルダ内にある「Sample2.osm」ファイルが処理され，「GMLs」フォルダ内に
「Sample2.gml」ファイル(RRSの地図データ形式)が生成される．という流れだ．

*[OSMの地図データの簡単な入手方法]*

「JOSM」というアプリケーションを使用すると良い．

「JOSM」のダウンロード先のURL：https://josm.openstreetmap.de/

このWebページ内の緑で塗りつぶされた領域に「Download josm-tested.jar」があるのでそこからダウンロードできる．

ダウンロードしたJOSMを開いたら地図が表示されるため，好きな領域をドラッグして選択し，「ダウンロード」ボタンを押す．

黒い背景に地図が表示されるようなウィンドウが表示されるので，左上にある保存ボタンを押してOSMファイルを保存する．


*[変換して作成されたRRS用の地図データの出来を確認する方法]*

RRSの地図データを編集するエディタ「gml-editor」を使用するため，RRSのプログラムをダウンロードする．

ダウンロードしたら，「gml-editor」で地図データを確認するだけである．

*[RRSのプログラムのダウンロード方法]*

以下のコマンドを実行するとカレントディレクトリに「roborescue-v1.2」というフォルダがダウンロードされる．

「curl -L -O https://raw.githubusercontent.com/tkmnet/rcrs-scripts/master/install-roborescue.sh」

「sh install-roborescue.sh」

以下のように表示されれば準備完了だ．

「BUILD SUCCESSFUL」

*[gml-editorを使った地図データの確認方法]*

コマンドラインで「roborescue-v1.2」フォルダ内へ移動し，以下のコマンドを入力

「apache-ant-[version]/bin/ant gml-editor」

gml-editorが起動する．

「gml-editor」の左上にある「Load」から，「osm2RRSgml」フォルダ内の「GMLs」フォルダ内にあるgmlファイルを選択する．

gml-editorの画面にRRS用の地図データが表示される．

gml-editorの右上にある「Validate map」を押すと地図データの中でエラーとなる箇所を赤く表示する．

赤くなった箇所はgml-editorを使用して修正しなければならない箇所である．

以上で，「osm2RRSgml」の使い方，OSMデータの入手方法，RRSの地図データの確認方法の説明を終わる．