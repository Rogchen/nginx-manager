/**
 * 异步请求封装
 */
if (!Layer) {
	var Layer = layer;
}
function Ajax() {
	this.cpage = 1;// 当前页
	this.rows = 10;// 每页条数
	this.isMorePage = true;// 判断是否需要翻页的服务(默认为真)
	this.ajaxFlag = null;// 用于防止同一服务被不小心连续触发
}
/**
 * 参数重置
 */
Ajax.prototype.reset = function() {
	this.cpage = 1;
	this.isMorePage = true;
};
/**
 * 目前object可选参数：
 * postLimit : 默认为true，是否限制同一个请求，在上一个请求成功前不允许执行下一个请求
 * load : 默认为undefined，全局显示加载提示，为false的时候不显示，为某个id的时候在对应id的div上显示
 */
Ajax.prototype.post = function(object) {
	// 部分默认值
	var obj = {
		type : "POST",
		dataType : "json",
		postLimit : true
	};
	var data = {
		cpage : this.cpage,
		rows : this.rows
	};
	$.extend(obj, object);
	if (object.needPage) {
		obj.data = $.extend(data, object.data);
	}
	//防止一次请求被执行多次（默认打开）
	if (obj.postLimit) {
		if (this.ajaxFlag == obj.url) {
			//	前一次请求未返回结果，默认取消本次请求
			return;
		}
		this.ajaxFlag = obj.url;
	}
	//生成加载提示
	var index = null;
	if (obj.load != false) {
		index = Layer.load();
	}
	var self = this;
	var ajaxObj = {
		success : function(data) {
			if (self.isTimeout(data)) {
				if (object && object.timeoutCallback) {
					return obj.timeoutCallback();
				}
				Layer.msg("登录超时，请重新登录");
				return;
			}
			if (object && object.success) {
				object.success(data);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if (object && object.error) {
				object.error(XMLHttpRequest, textStatus, errorThrown);
			} else {
				console.log('服务调用出错，出错信息：');
				console.log(XMLHttpRequest);
				console.log(textStatus);
				console.log(errorThrown);
			}
		},
		complete : function() {
			if (object && object.complete)
				object.complete();
			if (obj.postLimit) {
				self.ajaxFlag = null;
			}
			if (obj.load != false) {
				Layer.close(index);
			}
		}
	};
	$.extend(obj, ajaxObj);
	$.ajax(obj);
};
Ajax.prototype.get = function(object) {
    // 部分默认值
    var obj = {
        type : "GET",
        dataType : "json",
        postLimit : true
    };
    $.extend(obj, object);
    //防止一次请求被执行多次（默认打开）
    if (obj.postLimit) {
        if (this.ajaxFlag == obj.url) {
            //	前一次请求未返回结果，默认取消本次请求
            return;
        }
        this.ajaxFlag = obj.url;
    }
    //生成加载提示
    var index = null;
    if (obj.load != false) {
        index = Layer.load();
    }
    var self = this;
    var ajaxObj = {
        success : function(data) {
            if (self.isTimeout(data)) {
                if (object && object.timeoutCallback) {
                    return obj.timeoutCallback();
                }
                Layer.msg("登录超时，请重新登录");
                return;
            }
            if (object && object.success) {
                object.success(data);
            }
        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            if (object && object.error) {
                object.error(XMLHttpRequest, textStatus, errorThrown);
            } else {
                console.log('服务调用出错，出错信息：');
                console.log(XMLHttpRequest);
                console.log(textStatus);
                console.log(errorThrown);
            }
        },
        complete : function() {
            if (object && object.complete)
                object.complete();
            if (obj.postLimit) {
                self.ajaxFlag = null;
            }
            if (obj.load != false) {
                Layer.close(index);
            }
        }
    };
    $.extend(obj, ajaxObj);
    $.ajax(obj);
};
/**
 * 目前object可选参数：
 * postLimit : 默认为true，是否限制同一个请求，在上一个请求成功前不允许执行下一个请求
 * load : 默认为undefined，全局显示加载提示，为false的时候不显示，为某个id的时候在对应id的div上显示
 */
Ajax.prototype.ajaxSubmit = function(form, object) {
	$form = $("#" + form);
	var url = $form.attr("action");
	if (!url) {
		url = object.url;
	}
	var obj = {
		postLimit : true,
		type : "POST"
	};
	$.extend(obj, object);
	//防止一次请求被执行多次（默认打开）
	if (obj.submitLimit) {
		if (this.ajaxFlag == url) {
			//	前一次请求未返回结果，默认取消本次请求
			return;
		}
		this.ajaxFlag = url;
	}
	//生成加载提示
	var index = null;
	if (obj.load != false) {
		index = Layer.load();
	}
	var self = this;
	var ajaxObj = {
		type : obj.type,
		url : url,
		beforeSubmit : object.beforeSubmit,
		success : function(data) {
			if (self.isTimeout(data)) {
				if (object && object.timeoutCallback) {
					return obj.timeoutCallback();
				}
				Layer.msg("登录超时，请重新登录");
				return;
			}
			if (self.authDenied(data)) {
				Layer.msg("权限不足");
			}
			if (object && object.success) {
				object.success(data);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if (object && object.error) {
				object.error(XMLHttpRequest, textStatus, errorThrown);
			} else {
				console.log('服务调用出错，出错信息：');
				console.log(XMLHttpRequest);
				console.log(textStatus);
				console.log(errorThrown);
			}
		},
		complete : function() {
			if (object && object.complete)
				object.complete();
			if (obj.postLimit) {
				self.ajaxFlag = null;
			}
			if (obj.load != false) {
				Layer.close(index);
			}
		}
	};
	$form.ajaxSubmit(ajaxObj);
};
Ajax.prototype.loadHtml = function(object, parent) {
	var obj = {
		postLimit : true
	};
	var data = {
		cpage : this.cpage,
		rows : this.rows
	};
	$.extend(obj, object);
	if (object.needPage) {
		obj.data = $.extend(data, object.data);
	}
	//防止一次请求被执行多次（默认打开）
	if (obj.postLimit) {
		if (this.ajaxFlag == obj.url) {
			//	前一次请求未返回结果，默认取消本次请求
			return;
		}
		this.ajaxFlag = obj.url;
	}
	//生成加载提示
	var index = null;
	if (obj.load != false) {
		index = Layer.load();
	}
	var self = this;
	var $parent = parent ? $(parent) : $("<div><div>");
	$parent.load(obj.url, obj.data, function(html, status) {
		try {
			var result = JSON.parse(html);
			if (self.isTimeout(result)) {
				if (object && object.timeoutCallback) {
					return obj.timeoutCallback();
				}
				Layer.msg("登录超时，请重新登录");
			} else {
				Layer.msg(result.message);
			}
		} catch (e) {
			if (object && object.complete) {
				if (status == "error") {
					object.complete($("<div>请求失败...</div>"));
				} else {
					object.complete($(this));
				}
			}
		}
		if (obj.postLimit) {
			self.ajaxFlag = null;
		}
		if (obj.load != false) {
			Layer.close(index);
		}
	});
};
Ajax.prototype.ajaxSubmitWithValidate = function(form, object, validOption) {
	var layerTip;
	var self = this;
	var opt = $.extend({
		tiptype : function(msg, o, cssctl) {
			if (o.type == 3) {
				layerTip = layer.tips(msg, o.obj);
			} else {
                layerTip && layer.close(layerTip);
			}
		}
	}, validOption);
	opt.beforeSubmit = function() {
        self.ajaxSubmit(form, object);
		return false;
	};
	$("#" + form).Validform(opt);
}
//判断请求是否出错
Ajax.prototype.isError = function(data) {
	return data.code == "01";
};
//判断请求是否成功
Ajax.prototype.isSuccess = function(data) {
	return data.bizSuccess == true && data.code=="00";
};
//判断请求是否未查询到数据
Ajax.prototype.isNoResult = function(data) {
	return data.code == "900";
};
//判断请求是否登录超时
Ajax.prototype.isTimeout = function(data) {
	return data.code == "910";
};
//判断请求是否权限不足
Ajax.prototype.authDenied = function(data) {
	return data.code == "403";
};