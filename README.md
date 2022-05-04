# yuqing
投诉信息舆情
Web端为本系统主要的功能平台，是舆情地理可视化的重要展现方式。实现了以下功能：
Public opinion geography can be the main functional platform of the web-based system, and it is an important display method of public opinion geography visualization. The following functions are implemented:

Classification: geographical classification, real-time public opinion, latest alarm content, public opinion classification, time period selection Visualization items: heat map display, bubble chart display, time dynamic map display, area boundary display, bar chart display, pie chart display

The main purpose of the geographic visualization of public opinion in this system is to display the public opinion information on the map in the form of a heat map. Users can control the changes of the heat map by selecting the above categories. A darker area indicates a higher number of comments in that area over a certain period of time. The heatmap can be displayed by clicking on the first icon in the bottom black bar.

The visualization of the system is also reflected in the bubble chart that conforms to the trend of the times. Through background analysis, some words with high frequency appear in the form of bubble charts through the bubble chart template.

The system organizes the real-time dynamics (every 45s) and displays it with a time dynamic graph: the number of citizen comments in each category.
<img width="612" alt="image" src="https://user-images.githubusercontent.com/29689915/166726580-d306455c-25d9-4783-92b6-730f63dc2a6c.png">

分类：
地域分类、实时舆情、最新报警内容、舆情分类、时间段选择
可视化项目：
热力图显示、泡图显示、时间动态图显示、区域边界显示、柱状图显示、饼图显示
4.主要功能介绍

Web端为本系统主要的功能平台，是舆情地理可视化的重要展现方式。实现了以下功能：

分类：
地域分类、实时舆情、最新报警内容、舆情分类、时间段选择
可视化项目：
热力图显示、泡图显示、时间动态图显示、区域边界显示、柱状图显示、饼图显示

进入该网页
舆情地理可视化是将舆情信息通过分类，以地区为域的统计信息的形式展现在地图上，用户可以通过区域选择（杭州市内各区）、舆情分类（归属不同部门）、实时动态（选择时间段）来控制变化。

界面左上端的区域选择，可供选择的时间段一共是杭州的13个区，当用户选择不同的区域以后，就会展示相应地区内的舆情情况，未选择时默认为查看全部区域；
     
界面右上端的舆情分类的选择，将信息分为7类，分别是：治安、交通、环境、卫生、教育、宗教和其他，根据选择显示相应类别的信息，未选择时默认为查看全部信息，其他相对应的模块随之改变；
     
界面右上端的实时动态的选择，提供5个时间段选择，分别是实时动态、最近1天、最近3天、最近1周、最近1月，根据选择显示相应时间段的信息，未选择时默认实时；
                              
本系统可以自己定义地图的中心和缩放级别，本系统直接在地图初始化的时候传入了相关的属性，其经度值在前，纬度值在后；
可以通过搜索栏搜索地址，显示相应区的主要舆情动态，该搜索栏有记忆功能；
                       

本系统的舆情地理可视化最主要是将舆情信息通过热力图的形式展现在地图上，用户可以通过选择以上分类来控制热力图的变化。颜色越深的地区表示在某段时间内该地区发表评论的数量越多。热力图可以通过点击最下方黑色栏的第一个图标显示。

该系统的可视化还体现在顺应时代潮流的泡图上，通过后台分析，将出现频率较高的一些词汇通过泡图模板以泡图形式出现。
 

该系统将实时动态（每45s）整理，用时间动态图展示：各个类别市民评论的数量。
  
该系统对进行舆情分析的地区可进行区域边界化，可与热力图重叠。


--
