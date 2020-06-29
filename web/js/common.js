var baseUrl = "";
var _commonApp = {};
$(function() {
	//    下拉单选
	single();
	//    下拉多选
	multiChoose();
	// 枚举单选多选
	//    枚举出来的多选
	$('.fn-muiltchoice .muiltchoice-item').click(function() {
		$(this).toggleClass('muiltchoice-item-selected');
	});
	//    枚举出来的单选
	$('.fn-singlechoice  .singlechoice-item').click(function() {
		$(this).parents('.fn-singlechoice').find(' .singlechoice-item').removeClass('singlechoice-item-selected');
		$(this).addClass('singlechoice-item-selected');
	});
});

function single() {
	//    下拉单选选择
	$(".fn-selectInput").click(function() {
		$(this).parents("td").find(".fn-selectInputUl").slideDown("fast");
	});
	$(".fn-selectInput").parents("td").mouseleave(function() {
		$(this).find(".fn-selectInputUl").slideUp("fast");
	});
	//    将选择的值填充到单选框中去
	$(".fn-selectInputUl li").click(function() {
		var selectUlContent = $(this).text();
		$(this).parents("td").find(".selectInput").val(selectUlContent);
	});
}
// 多选还存在一点bug,bug问题在于取消全选之后，再选的话，前面有个逗号
function multiChoose() {
	//下拉多选择
	$(".fn-selectInput").click(function() {
		$(this).parents("td").find(".fn-pulldownSelect").slideDown("fast");
	});
	$(".fn-selectInput").parents("td").mouseleave(function() {
		$(this).find(".fn-pulldownSelect").slideUp("fast");
	});
	$(".selectInput-moreselectUl li .checkbox").click(function() {
		var moreselectUlContent = $(this).parents("li").find("font").text();
		var text = $(this).parents("td").find(".selectInput").val();
		if(text == "请选择") {
			$(this).parents("td").find(".selectInput").val(moreselectUlContent);
		} else {
			if($(this).prop('checked')) {
				if(text.indexOf(moreselectUlContent) > 0) {
					//                    证明了表单选择项里面有这个选项，我们不应该再将这项内容添加进去
					return;
				} else {
					$(this).parents("td").find(".selectInput").val(text + "," + moreselectUlContent);
				}
			} else {
				var start = text.indexOf(moreselectUlContent);
				var end = moreselectUlContent.length + start;
				if(text.charAt(start - 1) == ',') {
					text = text.substring(0, start - 1) + text.substring(end);
				} else {
					text = text.substring(end + 1);
				}
				//                如果取消勾选，应该从表单内容项里面移除该项
				$(this).parents("td").find(".selectInput").val(text);
			}
		}
	});
	//    全选
	$(".allSelectBtn").click(function() {
		var content = "";
		var length = $(".selectInput-moreselectUl li").size();
		$(".selectInput-moreselectUl li").each(function(index) {
			$(this).find(".checkbox").prop('checked', true);
			if(index != length - 1) {
				content += $(this).find("font").text() + ",";
			} else {
				content += $(this).find("font").text();
			}
		});
		$(this).parents("td").find(".selectInput").val(content);
	});
	//    取消全选
	$('.allSelect-clear').click(function() {
		$(this).parents("td").find(".selectInput").val("请选择");
		$(".selectInput-moreselectUl .checkbox").prop('checked', false);
	});
}
//获取multiselect多选值或者单选值，返回数组
function getMultiselect(id) {
	var $ele = $('#' + id);
	var v = [];
	var value = $ele.val();
	//    获取多选值
	if($ele.next().is('.fn-pulldownSelect')) {
		v = value.split(',');
	} else {
		//    获取单选值 
		v.push(value);
	}
	return v;
}

/** 
 * @description ajax jsonp请求，默认参数[type=GET] 
 */
function getJSONPData(param) {
	param = $.extend({
		url: '',
		type: 'get',
		jsonp: 'callback',
		dataType: 'jsonp',
		success: function() {}
	}, param);
	$.ajax(param);
}

/**
 * 根据Key获取地址栏全部参数
 */
function GetQueryParams() {
	var search = location.search.slice(1);
	var params = {};
	var arr = search.split("&");
	for(var i = 0; i < arr.length; i++) {
		var ar = arr[i].split("=");
		params[ar[0]] = ar[1];
	}
	return params;
}


/** 
 * @description 获取url参数 
 */
function getUrl(param) {

	param = param || {};
	var queryParam = '?';
	for(var i in param) {
		if(param[i]) {
			queryParam += (i + '=' + param[i] + '&')
		}
	}
	return queryParam.substring(0, queryParam.length - 1);
}

/** 
 * @description 打开弹窗 
 */
function openWin(param) {
	param = $.extend({
		type: 2,
		title: '',
		shadeClose: false,
		shade: 0.8,
		area: ['900px', '460px'],
		content: ''
	}, param);
	_commonApp.layerIndex = layer.open(param);
	_commonApp.layerSrc = param.content;
}
/**
 * 刷新layer
 */
function refreshCurLayer(){
	layer.iframeSrc(_commonApp.layerIndex, _commonApp.layerSrc);
}
