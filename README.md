# SimpleScreenRecord
Simple Screen Record for Android (os version&lt;4.4, need root)

use JavaCV and FrameBuffer to record screen

notice:
在生成apk之前，先add Native Support，并在CDT中build一次。

problem:
1.quite slow, and the rate of frame isn't stable, so need optimizing.
2.JavaCV has so many *.so library, and some of them aren't used in the project, which takes quite large space.
