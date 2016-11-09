# 乐聊
一个支持即时通讯的Android应用。这原本是自己的毕业设计，并以此拿到省优秀论文，现在已经毕业了，就把它开源了。消息的推送使用的是[Bmob后端云](http://www.bmob.cn/)，支持常见的消息类型的收发，并且在本地做了缓存处理，即使在没有网络的情况下，依然可以查看以往聊天消息历史纪录。加入了好友圈功能，支持纯文本、图片动态。（注：Bmob政策原因，现在免费用户上传文件API不能使用，因此，发送图片和语音功能受到限制）

# 功能
- 发送纯文本消息
- 发送emoji表情
- 发送地理位置
- ~~发送图片（支持本地相册或者拍照）~~
- ~~发送语音~~
- 支持搜索附近的人
- 支持将好友加入黑名单
- 支持好友圈发表动态

# Bmob Key & BaiduMap Key
```
 Bmob后端云的key位于common文件夹的Config.java中
 public static String applicationId = "";
 在这里替换为自己在Bmob官网申请的key
 
 百度地图的key位置AndroidManifest.xml文件中
  <meta-data
            android:name="com.baidu.lbsapi.API_KEY"
            android:value="6pmDLTeKWRSFhCcdEpkjsIiXA9EYujSV"/>
  建议替换为自己在百度地图开发者中心自己申请的开发者key
```
# 截图
<img src="screenshots/2016-11-04-230257.png" width=30% height=55% /><img src="screenshots/2016-11-04-230236.png" width=30% height=55% /><img src="screenshots/2016-11-04-230655.png" width=30% height=55% />
<img src="screenshots/2016-11-04-225812.png" width=30% height=55% /><img src="screenshots/2016-11-04-225654.png" width=30% height=55% /><img src="screenshots/2016-11-04-225520.png" width=30% height=55% /><img src="screenshots/2016-11-04-225947.png" width=30% height=55% /><img src="screenshots/2016-11-04-230614.png" width=30% height=55% /><img src="screenshots/2016-11-04-225833.png" width=30% height=55% />
# 依赖库
- [PhotView]()
- [ButterKnife]()
- [RecyclerView]()
- [Universal-Image-Loader]()
- [Logger]()

# 致谢
-	[Bmob后端云](http://www.bmob.cn/)

# 关于我
- 邮箱：tonycheng93@outlook.com
- 博客：http://tonycheng93.github.io/
- Github:https://github.com/tonycheng93/

# License
```
Copyright (c) 2016 tonycheng93

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
