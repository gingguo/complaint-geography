var app = {
    map: null,
    heatmapOverlay: null,
    heatData: [],
    heatKey: '',
    geocode: new T.Geocoder(),
    isDouble: false,
    loadSuccess: false,
    loadFun: null,
    heatFun: null,
    citymapOverlay: [],
    timerCount: 0, //多地市查询等待执行次数
    changeType:1,//查询触发类型
    fillColor:['#00FF66','#00FF33','#00FF00','#CCFF66','#FFFF66','#FFFF00','#996666','#CC6666','#FF6666','#FF6633','#FF3366','#FF3333','#FF3300'],//渐变色
    allCityId: '00',
    areaList: [{id:'02', text:'上城'},
        {id:'03', text:'下城'},
        {id:'04', text:'江干'},
        {id:'05', text:'拱墅'},
        {id:'06', text:'西湖'},
        {id:'08', text:'滨江'},
        {id:'09', text:'萧山'},
        {id:'10', text:'余杭'},
        {id:'22', text:'桐庐'},
        {id:'27', text:'淳安'},
        {id:'82', text:'建德'},
        {id:'11', text:'富阳'},
        {id:'12', text:'临安'}],
    categoryList:[{id:'1', text:'商品食品'},
        {id:'2', text:'医疗医保'},
        {id:'3', text:'交通车辆'},
        {id:'4', text:'自然资源'},
        {id:'5', text:'税法税收'},
        {id:'6', text:'人力资源'},
        {id:'7', text:'社会保障'},
        {id:'8', text:'街道管理'},
        {id:'9', text:'其他类别'}],
    cityData: [],
    shangCheng: {
        "lnt": 120.16462500004,
        "adminType": "county",
        "englishabbrevation": "Shangcheng Dist",
        "nameabbrevation": "上城",
        "level": 12,
        "cityCode": "156330102",
        "bound": "120.132424,30.260752,120.2097,30.195382",
        "name": "上城区",
        "english": "Shangcheng District",
        "lat": 30.24493700024,
        "points": [{
            "region": "120.164 30.261,120.183 30.26,120.183 30.255,120.183 30.253,120.183 30.253,120.181 30.249,120.184 30.248,120.187 30.248,120.193 30.245,120.197 30.242,120.21 30.232,120.201 30.224,120.171 30.208,120.162 30.202,120.156 30.2,120.135 30.195,120.132 30.2,120.134 30.204,120.134 30.207,120.139 30.208,120.135 30.216,120.143 30.215,120.149 30.22,120.15 30.218,120.155 30.226,120.155 30.233,120.152 30.235,120.15 30.235,120.149 30.238,120.15 30.243,120.155 30.248,120.153 30.249,120.155 30.252,120.158 30.253,120.155 30.258,120.153 30.26,120.154 30.261,120.159 30.261,120.164 30.261"
        }],
        "parents": {
            "country": {
                "adminType": "country",
                "cityCode": "156000000",
                "name": "中华人民共和国"
            },
            "province": {
                "adminType": "province",
                "cityCode": "156330000",
                "name": "浙江省"
            },
            "city": {
                "adminType": "city",
                "cityCode": "156330100",
                "name": "杭州市"
            }
        }
    }
};

$(function() {
    // 调整页面大小
    setPage();
    initMap();
    initElement();
    // 加载图表数据
    intervalLoad();

    // 15分钟刷新右侧栏数据
    setInterval(function() {
        intervalLoad();
    }, 15 * 60 * 1000);
});

function intervalLoad(){
    laodChartBar();
    loadChartPie();
    loadRealAlarm();
    loadNewestCate();
}

function initMap(){
    // 设置地图
    app.map = new T.Map("iMap");
    //设置显示地图的中心点和级别
    app.map.centerAndZoom(new T.LngLat(120.1503898, 30.27619907), 12);
    app.map.checkResize();
    app.map.on("click", function(e) {

        app.heatFun = setTimeout(function() {
            app.geocode.getLocation(e.lnglat, convertLngLat);
        }, 300)

    }).on('dblclick', function(e) {
        // 解决触发单击事件
        clearTimeout(app.heatFun);
    });
}

function initElement() {

    $('.m_reset_body').height(($(".m-container").height() - $('.m_card_title').height() * 2) / 2 - 13);

    //生成区域数据
    var htmlArr = [];
    for (var i=0; i<app.areaList.length; i++) {
        htmlArr.push('<li value="'+app.areaList[i].id+'">'+app.areaList[i].text+'</li>');
    }
    $("#dropArea").append(htmlArr.join(''));

    //生成舆情数据
    htmlArr = [];
    for (var i=0; i<app.categoryList.length; i++) {
        htmlArr.push('<li value="'+app.categoryList[i].id+'">'+app.categoryList[i].text+'</li>');
    }
    $("#dropOpinionsType").append(htmlArr.join(''))

    $(window).bind('resize', resizebind);
    // 选项卡的切换
    $('.fn-tab ul li').click(function() {
        var $index = $(this).index();
        $(this).siblings().removeClass('select');
        $(this).addClass('select');
        var $tab_content = $(this).parent().parent().parent().find('.m_tab_content');
        $tab_content.hide();
        $tab_content.eq($index).show();
    });

    // 下拉框条件选择
    $('.city-list,.option-content').on('click', 'li', function() {
        var obj = $(this);
        app.changeType = obj.parent().attr("type");
        obj.parent().children().attr('checked', false);
        obj.attr('checked', true);
        var p = obj.parents('.option-content,.city-list').prev().children('p');
        p.text(obj.text());
        p.attr('value', obj.attr('value'));

        loadData(true);
        //如果是实时动态，则需要15分钟刷新一次
        if(app.changeType == 3){
            var last = $('#datecycle').attr('value');
            if(!last){
                app.timerLoad = setInterval(function() {
                    loadData(false);
                }, 1000 * 60 * 15);
            }else{
                clearInterval(app.timerLoad);
            }
        }
    });

    // 时间选择
    $('#txtDate').click(function() {
        WdatePicker({onpicked:function() {
                app.changeType = 4;
                loadData(true);
            }});
    });

    $('#div-search').on('click', function(event) {
        // 阻止事件冒泡 触发 map点击事件
        event.stopPropagation();
    });
    // 清除搜索框
    $('#div-picicon').click(function() {
        $('#keyword').val('');
    });
    // 搜索
    $("#btnSearch").click(function(event) {
        $(".search .picicon").toggleClass("loadingicon");
        // 阻止事件冒泡 触发 map点击事件
        event.stopPropagation();
        loadData(true);
        // 数据加载完 加载图标改变
        app.loadFun = setInterval(function() {
            if(app.loadSuccess) {
                $(".search .picicon").toggleClass("loadingicon");
                clearInterval(app.loadFun);
            }
        }, 1000);
    });
    // 展开 收缩
    $('.left-open-switch').click(function(e) {
        // 阻止事件冒泡 触发 map点击事件
        event.stopPropagation();
        $('.left-nav').toggleClass('width-transformation');
        $(this).toggleClass('left-close-switch');
        setPage();
    });
    $('.right-open-switch').click(function(e) {
        // 阻止事件冒泡 触发 map点击事件
        event.stopPropagation();
        $('.right-nav').toggleClass('width-transformation');
        $(this).toggleClass('right-close-switch');
        setPage();
    });

    // 弹窗
    $('#btnFeature').click(function() {
        openWin({
            title: '舆情特征',
            area: ['800px', '460px'],
            content: 'feature.html'
        });
    });
    $('#btnDynamic').click(function() {
        openWin({
            title: '时间动态',
            area: ['900px', '460px'],
            content: 'dynamic.html'
        });
    })
    $('#btnUnit').click(function() {
        openWin({
            title: '特征信息',
            area: ['900px', '400px'],
            content: 'unit.html'
        });
    })
    $('#btnTree').click(function() {
        openWin({
            title: '树状结构图',
            area: ['900px', '460px'],
            content: 'tree.html'
        });
    });
    // 加载热力图
    $('#btnHeatmap').click(function() {
        loadHeatMap();
    });
    // 加载区县边界图
    $('#btnCityBorder').click(function() {
        loadCityBorder();
    });
    // 清除地图覆盖物
    $('#btnClearMap').click(function() {
        app.map.clearOverLays();
        app.heatmapOverlay = null;
        app.citymapOverlay = [];
    });
}


/**
 * @description 获取参数
 */
function getParam() {

    var distId = 3301;
    var keyword = $('#keyword').val();
    var cityId = $('#cityId').attr('value') || app.allCityId;
    var publicInfo = $('#publicInfo').attr('value');
    var last = $('#datecycle').attr('value') || '0';
    var datetime = $('#txtDate').val();
    var resolution = 500;

    var param = {
        citycode: distId,
        keyword: keyword,
        name: keyword,
        last: last,
        date: datetime,
        category: publicInfo,
        distinct: cityId,
        resolution: resolution
    };
    //如果最后选择日期，则使用日期查询，否则使用时间类型查询
    if(app.changeType == 4){
        param.date = datetime;
    }
    return param;
}

/**
 * @description 加载部分初始化数据
 */
function loadData(isRefresh) {
    if(isRefresh == true){
        intervalLoad();
    }
    // 如果界面存在 热力图则查询加载
    if(app.heatmapOverlay) {
        loadHeatMap();
    }
    // 如果界面存在 区县边界图则查询加载
    if(app.citymapOverlay.length > 0) {
        loadCityBorder();
    }
}

function resizebind() {
    setPage();
}

function setPage() {
    // 调整中间主体内容高度
    // $(".m-container").height($(window).outerHeight()-$(".m-header").outerHeight()-$('.m-footer').outerHeight());
    $(".m-container").height($(window).height() - $(".m-header").height() - $('.m-footer').height());
    // 调整地图显示层的宽度
    $('.center-map').width($('body').width() - $('.left-nav').width() - $('.right-nav').width());
    if(app.map) {
        app.map.checkResize();
    }
};

/**
 * @description 加载区县边界
 */
function loadCityBorder() {
    app.cityData = [];
    app.timerCount = 0;

    if(app.citymapOverlay.length > 0) {
        app.citymapOverlay.map(function(i) {
            return app.map.removeOverLay(i);
        })
        app.citymapOverlay = [];
    }
    var param = getParam();
    if(param.distinct == app.allCityId){
        //多地市
        for (var i=0; i<app.areaList.length; i++) {
            param.distinct = app.areaList[i].id;
            getJSONPData({
                url: baseUrl + '/ds/distinct' + getUrl(param),
                jsonpCallback: 'setCity' + app.areaList[i].id
            });
        }
    }else{
        getJSONPData({
            url: baseUrl + '/ds/distinct' + getUrl(param),
            jsonpCallback: 'setCity' + param.distinct
        });
    }

    //定时执行，等待所有请求完成
    var timer = setInterval(function(){
        app.timerCount += 1;
        if(app.timerCount >= 60){
            clearInterval(timer);
        }
        if(param.distinct == app.allCityId){
            if(app.cityData.length == app.areaList.length){
                clearInterval(timer);
                setCity();
            }
        }else if(app.cityData.length > 0){
            clearInterval(timer);
            setCity();
        }

    }, 1000);
};

function setCity02(data){
    data = $.extend({name:"上城", status: 0}, data);
    app.cityData.push(data);
}
function setCity03(data){
    data = $.extend({name:"下城", status: 0}, data);
    app.cityData.push(data);
}
function setCity04(data){
    data = $.extend({name:"江干", status: 0}, data);
    app.cityData.push(data);
}
function setCity05(data){
    data = $.extend({name:"拱墅", status: 0}, data);
    app.cityData.push(data);
}
function setCity06(data){
    data = $.extend({name:"西湖", status: 0}, data);
    app.cityData.push(data);
}
function setCity08(data){
    data = $.extend({name:"滨江", status: 0}, data);
    app.cityData.push(data);
}
function setCity09(data){
    data = $.extend({name:"萧山", status: 0}, data);
    app.cityData.push(data);
}
function setCity10(data){
    data = $.extend({name:"余杭", status: 0}, data);
    app.cityData.push(data);
}
function setCity22(data){
    data = $.extend({name:"桐庐", status: 0}, data);
    app.cityData.push(data);
}
function setCity27(data){
    data = $.extend({name:"淳安", status: 0}, data);
    app.cityData.push(data);
}
function setCity82(data){
    data = $.extend({name:"建德", status: 0}, data);
    app.cityData.push(data);
}
function setCity11(data){
    data = $.extend({name:"富阳", status: 0}, data);
    app.cityData.push(data);
}
function setCity12(data){
    data = $.extend({name:"临安", status: 0}, data);
    app.cityData.push(data);
}

/**
 * @description  获取区县边界图数据
 */
function setCity() {
    sortCityData();
    var administrative = new T.AdministrativeDivision();
    var word = $('.city-list li[checked=checked]').text() || "杭州";
    var config = {
        needSubInfo: true,
        needAll: false,
        needPolygon: true,
        needPre: true,
        searchType: 1,
        searchWord: word
    };
    administrative.search(config, searchResult);
}

var objectArraySort = function (keyName) {
    return function (objectN, objectM) {
        var valueN = objectN[keyName]
        var valueM = objectM[keyName]
        if (valueN < valueM) return 1
        else if (valueN > valueM) return -1
        else return 0
    }
}

//排序数据,根据综合排序
function sortCityData(){
    for (var i = 0; i < app.cityData.length; i++) {
        var hotArr = app.cityData[i].hot;
        var sumnum = 0;
        for (var j = 0; j < hotArr.length; j++) {
            sumnum += hotArr[j].number;
        }
        app.cityData[i].sumnum = sumnum;
    }

    app.cityData.sort(objectArraySort('sumnum'))
}

/**
 * @description 处理返回的边界图数据
 */
function searchResult(result) {

    if(result.getStatus() == 100) {

        var data = result.getData();
        showMsg(data);

        //显示 杭州 最佳比例尺
        if(data && data.length > 0){
            var pointArr = [];
            for(var i = 0; i < data[0].points.length; i++) {
                var regionLngLats = [];
                var regionArr = data[0].points[i].region.split(",");
                for(var m = 0; m < regionArr.length; m++) {
                    var point = regionArr[m].split(" ");
                    var lnglat = new T.LngLat(point[0], point[1]);
                    regionLngLats.push(lnglat);
                    pointArr.push(lnglat);
                }
            }
            app.map.setViewport(pointArr);
        }
    } else {
        result.getMsg();
    }
};

/**
 * @description 边界图多级 处理
 */
function showMsg(data) {

    for(var i = 0; i < data.length; i++) {

        if(!data[i].child && data[i].points) {
            // 绘制行政区划
            polygon(data[i]);
        }
        // 解释下级行政区划
        if(data[i].child) {
            var childs = data[i].child;
            if(childs.length <= 12) {
                childs.push(app.shangCheng);
            }
            showMsg(childs);
            if(childs.points) {
                // 绘制行政区划
                polygon(childs);
            }
        }
    }
}

function getCityDataByName(name){
    for (var i = 0; i < app.cityData.length; i++) {
        if(name.indexOf(app.cityData[i].name) > -1){
            app.cityData[i].index = i;
            return app.cityData[i];
        }
    }
}

/**
 * @description 绘制区县边界图
 */
function polygon(data) {

    var points = data.points || [];
    var pointArr = [];
    var cityData = getCityDataByName(data.name);
    var colorIndex = cityData.index;

    for(var i = 0; i < points.length; i++) {
        var regionLngLats = [];
        var regionArr = points[i].region.split(",");
        for(var m = 0; m < regionArr.length; m++) {
            var point = regionArr[m].split(" ");
            var lnglat = new T.LngLat(point[0], point[1]);
            regionLngLats.push(lnglat);
            pointArr.push(lnglat);
        }
        //创建面对象
        var polygon = new T.Polygon(regionLngLats, {
            color: "#fff",
            weight: 1,
            opacity: 0.7,
            fillColor: app.fillColor[colorIndex],
            fillOpacity: 0.6
        });
        polygon.on('mouseover', function(e) {
            $('.common-panel-title').text(data.name);
            var temp = '';
            for(var i = 0; i < cityData.hot.length; i++) {
                var obj = cityData.hot[i];
                temp += '<li class="panel-list"> <span class="list-title">' + getCategory(obj.category) + '：</span> <span class="list-intro">' + obj.number + '</span> </li>';
            }
            $('.panel-1').html(temp);
            polygon.setStyle({
                dashArray: '0',
                weight: 2,
                color: '#0002fb'
            });
        }).on('mouseout', function() {
            polygon.setStyle({
                dashArray: '0',
                weight: 2,
                color: '#fff'
            });
            $('.property_popup').css({
                top: 10000
            });
        }).on('mousemove', function(e) {
            var ab = e.originalEvent.pageX + 25 + "px";
            var aa = e.originalEvent.pageY + "px";
            $('.property_popup').css({
                left: ab,
                top: aa
            });
        })
        app.citymapOverlay.push(polygon);
        //向地图上添加行政区划面
        app.map.addOverLay(polygon);
    }

};

/**
 * @description 加载热力图
 *
 */
function loadHeatMap() {

    if(app.heatmapOverlay) {
        app.map.removeOverLay(app.heatmapOverlay);
    }
    getJSONPData({
        url: baseUrl + '/ds/heatmap' + getUrl(getParam()),
        jsonpCallback: 'setHeatMap'
    });
    /*var data = {
      "status": 1,
      "info": "OK",
      "heats": [
        {
          "location": [120.1646498, 30.24463324],
          "number": 110
        },
        {
          "location": [120.1367192, 30.32136309],
          "number": 98
        },
        {
          "location": [120.125546, 30.261546],
          "number": 200
        }
      ]
    };

    setHeatMap(data);*/
};

/**
 * @description  设置热力图数据
 */
function setHeatMap(data) {

    if(data && data.status == 1) {

        var points = convertHeatData(data.heats || []);
        app.heatData = points;
        app.heatmapOverlay = new T.HeatmapOverlay({
            "radius": 30,
        });
        app.map.addOverLay(app.heatmapOverlay);
        app.heatmapOverlay.setDataSet({
            data: points,
            max: 300
        });
        // 重新定位 避免热力图错位  或不显示问题
        app.map.centerAndZoom(new T.LngLat(120.1503898, 30.27619907), 12);
    }
};

/**
 * @description 逆地址解析 调用舆情特征信息
 */
function convertLngLat(data) {

    if(data && data.status == 0) {
        var json = data.addressComponent;
        if(json.city.indexOf('杭州') >= 0) {
            var heatKey = json.address;
            openWin({
                title: '特征信息',
                area: ['900px', '400px'],
                content: 'tree.html?keyword=' + heatKey
            });
        }
    }
}

/**
 * @description 转换热力图数据
 */
function convertHeatData(data) {

    var heatData = [];
    for(var i = 0; i < data.length; i++) {
        var location = data[i].location;
        if(location) {
            heatData.push({
                name: '',
                lat: location[1],
                lng: location[0],
                count: data[i].number
            });
        }
    }
    return heatData;
};

/**
 * @description  【功能09】分类型的实时舆情数量统计
 * @param param {Object}
 */

function loadChartPie() {
    var param = getParam();
    getJSONPData({
        url: baseUrl + '/ds/distinct' + getUrl(param),
        jsonpCallback: 'setPieData'
    });

    /*var data = {
      "status": 1,
      "info": "OK",
      "hot": [
        { category: 1, number: 12 },
        { category: 2, number: 11 },
        { category: 3, number: 55 },
        { category: 4, number: 5 },
        { category: 5, number: 6 },
        { category: 6, number: 7 },
        { category: 7, number: 7 }
      ]
    }
   setPieData(data);*/
};

/**
 * @description 设置环形图数据
 */
function setPieData(data) {

    if(data && data.status == 1) {

        var dataOpt = data.hot || [],
            dataArr = [];
        for(var i = 0; i < dataOpt.length; i++) {
            var obj = dataOpt[i];
            dataArr.push({
                name: getCategory(obj.category),
                value: obj.number
            });
        }

        var chartPie = echarts.init(document.getElementById('chart2'), 'light');

        var pieOptions = {
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            //color:[/*'#070093',*/'#1C3FBF','#1482E5','#70B4EB','#B4E0F3'],
            //legend: {
            //    x: 'center',
            //    data:[['政务','社区','交通','宗教','治安','医疗卫生', '环境']]
            //},
            grid: {
                show:'true',borderWidth:'0'
            },
            series: [{
                name: '类别',
                type: 'pie',
                radius : [30, 110],
                center : ['50%', '50%'],
                roseType : 'area',
                data: dataArr
            }]
        };
        chartPie.setOption(pieOptions);
    }
};

/**
 * @description  【功能08】分行政区实时舆情数量统计
 * @param param {Object}
 */

function laodChartBar() {
    app.cityBarData = [];
    app.timerCount08 = 0;

    var param = getParam();
    //多地市
    for (var i=0; i<app.areaList.length; i++) {
        param.distinct = app.areaList[i].id;
        getJSONPData({
            url: baseUrl + '/ds/distinct' + getUrl(param),
            jsonpCallback: 'setBarData' + app.areaList[i].id
        });
    }
    //定时执行，等待所有请求完成
    var timer = setInterval(function(){
        app.timerCount08 += 1;
        if(app.timerCount08 >= 60){
            clearInterval(timer);
        }

        if(app.cityBarData.length == app.areaList.length){
            clearInterval(timer);
            setBarData();
        }
    }, 1000);

    /*var data = {
      "status": 1,
      "info": "OK",
      "hot": [
        { category: 1, number: 12 },
        { category: 2, number: 11 },
        { category: 3, number: 55 },
        { category: 4, number: 5 },
        { category: 5, number: 6 },
        { category: 6, number: 7 },
        { category: 7, number: 7 }
      ]
    }

    setBarData(data);*/
}

function setBarData02(data){
    data = $.extend({name:"上城", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData03(data){
    data = $.extend({name:"下城", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData04(data){
    data = $.extend({name:"江干", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData05(data){
    data = $.extend({name:"拱墅", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData06(data){
    data = $.extend({name:"西湖", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData08(data){
    data = $.extend({name:"滨江", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData09(data){
    data = $.extend({name:"萧山", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData10(data){
    data = $.extend({name:"余杭", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData22(data){
    data = $.extend({name:"桐庐", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData27(data){
    data = $.extend({name:"淳安", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData82(data){
    data = $.extend({name:"建德", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData11(data){
    data = $.extend({name:"富阳", status: 0}, data);
    app.cityBarData.push(data);
}
function setBarData12(data){
    data = $.extend({name:"临安", status: 0}, data);
    app.cityBarData.push(data);
}

/**
 * @description 设置柱状图数据
 */
function setBarData() {

    if(app.cityBarData.length > 0) {
        var yAxisData = [];
        for (var i = 0; i < app.cityBarData.length; i++) {
            yAxisData.push(app.cityBarData[i].name);

        }

        var seriesArr = [];
        for (var i=0; i<app.categoryList.length; i++) {
            var bar = {name:app.categoryList[i].text, type: 'bar', stack: '总量',data:[]};
            for (var j = 0; j < app.cityBarData.length; j++) {
                for (var k = 0; k < app.cityBarData[j].hot.length; k++) {
                    if(app.categoryList[i].id == app.cityBarData[j].hot[k].category){
                        bar.data.push(app.cityBarData[j].hot[k].number);
                        break;
                    }
                }

            }
            seriesArr.push(bar);
        }

        var barOptions = {
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '2%',
                right: '2%',
                bottom: 0,
                top: '7%',
                show: false,
                containLabel: true
            },
            xAxis: {
                show: false,
                offset: 10,
                type: 'value',
                splitLine: {
                    show: false
                }, //去除网格线
                axisLine: {
                    show: false
                }, //隐藏y轴
                axisTick: {
                    show: false
                },
                boundaryGap:true,
                axisLabel:{
                    margin:0
                }
            },
            yAxis: {
                type: 'category',
                data: yAxisData,
                splitLine: {
                    show: false
                }, //去除网格线
                axisLine: {
                    show: false
                }, //隐藏y轴线
                axisTick: {
                    show: false
                }, //隐藏刻度
                axisLabel: {
                    show: true
                }, //隐藏标签
                axisLabel: {
                    textStyle: {
                        color: '#999999'
                    }
                }
            },
            series: seriesArr
        };


        var chartBar = echarts.init(document.getElementById('chart3'), 'light');
        chartBar.setOption(barOptions);
    }
};


/**
 * @description 获取舆情中文名
 */
function getCategory(id) {
    var cate = '不限类别';
    for (var i=0; i<app.categoryList.length; i++) {
        if(id == app.categoryList[i].id){
            return app.categoryList[i].text;
        }
    }
    return cate;
}

/**
 * @description 加载实时报警信息
 */
function loadRealAlarm() {

    getJSONPData({
        url: baseUrl + '/ds/alarm' + getUrl(getParam()),
        jsonpCallback: 'setRealAlarm'
    });
    /*var data = {
      "status": 1,
      "info": "OK",
      "events": [
        {
          "time": "2018-06-11 13:34:57",
          "name": ["张三", "李四"],
          "organization": ["发改委"],
          "category": 0,
          "content": "两个人中午在饭馆吃饭，肯定公款。",
          "keywords": ["公款", "吃饭"]
        },
        {
          "time": "2018-06-11 14:21:34",
          "name": ["王五"],
          "organization": ["城管", "派出所"],
          "category": 1,
          "content": "城管和警察打人了，就在派出所门口！",
          "keywords": ["打人", "暴力"]
        }
      ]
    };
    setRealAlarm(data);*/
};

/**
 * @description 设置实时报警信息
 */
function setRealAlarm(data) {

    if(data && data.status == 1) {

        var dataOpt = data.events || [];
        var htmlArr = [];
        htmlArr.push('<tr><td>时间</td><td>人名</td><td>机构名</td><td>舆情分类</td><td>舆情内容</td><td>关键词</td></tr>');
        for(var i = 0; i < dataOpt.length; i++) {
            var obj = dataOpt[i];
            htmlArr.push('<tr><td>');
            htmlArr.push(obj.time);
            htmlArr.push('</td><td>');
            htmlArr.push(obj.name.join('，'));
            htmlArr.push('</td><td>');
            htmlArr.push(obj.organization.join('，'));
            htmlArr.push('</td><td>');
            htmlArr.push(getCategory(obj.category));
            htmlArr.push('</td><td style="text-align: left">');
            htmlArr.push(obj.content);
            htmlArr.push('</td><td>');
            htmlArr.push(obj.keywords.join('，'));
            htmlArr.push('</td></tr>');
        }
        $('#realAlarm').html(htmlArr.join(''));
    }
};

/**
 * @description 加载最新舆情
 */
function loadNewestCate() {

    getJSONPData({
        url: baseUrl + '/ds/rolling' + getUrl(getParam()),
        jsonpCallback: 'setNewestCate'
    });
    /*var data = {
      "status": 1,
      "info": "OK",
      "events": [
        {
          "time": "2018-06-11 13:34:57",
          "category": 0,
          "content": "两个人中午在饭馆吃饭，肯定公款。"
        },
        {
          "time": "2018-06-11 14:21:34",
          "category": 1,
          "content": "城管和警察打人了，就在派出所门口！"
        }
      ]
    };
    setNewestCate(data);*/
};

/**
 * @description
 */
function setNewestCate(data) {

    if(data && data.status == 1) {

        var dataOpt = data.events || [];
        var htmlArr = [];
        htmlArr.push('<tr><td>时间</td><td>舆情分类</td><td>舆情内容</td></tr>');
        for(var i = 0; i < dataOpt.length; i++) {
            var obj = dataOpt[i];
            htmlArr.push('<tr><td>');
            htmlArr.push(obj.time);
            htmlArr.push('</td><td>');
            htmlArr.push(getCategory(obj.category));
            htmlArr.push('</td><td style="text-align: left">');
            htmlArr.push(obj.content);
            htmlArr.push('</td></tr>');
        }
        $('#newestCate').html(htmlArr.join(''));
        app.loadSuccess = true;
    }
};