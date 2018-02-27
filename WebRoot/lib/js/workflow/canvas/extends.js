/**
 * HTML CANVAS 绘图扩展部分
 */
/**
 *圆角矩形
 * @param x:x坐标
 * @param y:y坐标
 * @param w：宽度
 * @param h：高度
 * @param r：圆角半径
 * @returns {CanvasRenderingContext2D}
 */
CanvasRenderingContext2D.prototype.roundRect = function (x, y, w, h, r) {
    if (w < 2 * r) r = w / 2;
    if (h < 2 * r) r = h / 2;
    this.beginPath();
    this.moveTo(x + r, y);
    this.arcTo(x + w, y, x + w, y + h, r);
    this.arcTo(x + w, y + h, x, y + h, r);
    this.arcTo(x, y + h, x, y, r);
    this.arcTo(x, y, x + w, y, r);
    // this.arcTo(x+r, y);
    this.closePath();
    return this;
}

/**
 * 四边形
 * @param x
 * @param y
 * @param w
 * @param h
 * @param offset-->正面边向左或向右偏移（）
 * @returns {CanvasRenderingContext2D}
 */
CanvasRenderingContext2D.prototype.quadrangle = function (x, y, w, h, offset) {
    this.beginPath();
    var points = [
        {x: x, y: y}, {x: x + w, y: y}, {x: x - offset, y: y + h}, {x: x + w - offset, y: y + h}
    ];
    this.moveTo(x, y);
    this.lineTo(points[1].x, points[1].y);
    this.lineTo(points[3].x, points[3].y);
    this.lineTo(points[2].x, points[2].y);
    // this.arcTo(x+r, y);
    this.closePath();
    return this;
}


/**
 *菱形
 * @param x
 * @param y
 * @param w
 * @param h
 * @returns {CanvasRenderingContext2D}
 */
CanvasRenderingContext2D.prototype.rhombus = function (x, y, w, h) {
    this.beginPath();
    var points = [
        {x: x + w / 2, y: y}, {x: x + w, y: y + h / 2}, {x: x + w / 2, y: y + h}, {x: x, y: y + h / 2}
    ];
    this.lineTo(points[0].x, points[0].y);
    this.lineTo(points[1].x, points[1].y);
    this.lineTo(points[2].x, points[2].y);
    this.lineTo(points[3].x, points[3].y);
    // this.arcTo(x+r, y);
    this.closePath();
    return this;
}

/**
 * 相关工具函数扩展部分
 */
/**
 * 对象HashTable
 * @constructor
 */
var HashTable = function () {
    var map = new Array();
    this.map = function () {
        return map;
    };
    this.add = function (key, value) {
        map[key] = value;
    };
    this.remove = function (key) {
        delete map[key];
    };

    this.removeAll = function () {
        map = new Array();
    };
    this.get = function (key) {
        if (map[key] == null || map[key] == undefined) {
            return null;
        }
        else {
            return map[key];
        }
    };
    this.getKey = function (value) {
        for (var key in map) {
            if (map[key] == value) {
                return key;
            }
        }
        return null;

    };
    this.keys = function () {
        var keyList = new Array();
        var count = 0;
        for (var key in map) {
            keyList[count] = key;
            count++;
        }
        return keyList;
    };
    this.values = function () {
        var valueList = new Array();
        var count = 0;
        for (var key in map) {
            if(typeof map[key] == 'function'){
                continue;
            }
            valueList[count] = map[key];
            count++;
        }
        return valueList;
    };
};


/**
 * guid工具函数
 * @returns {string}
 */
var newGuid = function () {
    var guid = "";
    for (var i = 1; i <= 32; i++) {
        var n = Math.floor(Math.random() * 16.0).toString(16);
        guid += n;
        if ((i == 8) || (i == 12) || (i == 16) || (i == 20))
            guid += "-";
    }
    return guid;
};

/**
 *获取两点之间的距离
 */
var getPointDistance = function (point1, point2) {
    //Math.pow((xdiff * xdiff + ydiff * ydiff), 0.5);
    return round2(Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2)), 2);
};
/**
 * //四舍五入
 * @param number
 * @param fractionDigits
 * @returns {number}
 */
var round2 = function (number, fractionDigits) {
    with (Math) {
        return round(number * pow(10, fractionDigits)) / pow(10, fractionDigits);
    }
};

/**
 * 获取点阵points中离点point最近的点
 * @param point
 * @param points
 */
var getNearestPoint = function (point, points) {
    var min_dis = getPointDistance(point, points[0]);
    var cp = points[0];
    for (var i = 1; i < points.length; i++) {
        var tpd = getPointDistance(point, points[i]);
        if (min_dis > tpd) {
            min_dis = tpd;
            cp = points[i];
        }
    }
    return cp;
}

/*
 * $extend:
 * 		用object扩展src,并返返回扩展对象object
 * params:
 *      object:子对象，它扩展src,并继承了 src的所有功能
 *      src:父对象，它是创建object对象的模板
 * return:
 *       object返回object
 */
function $extend(object, src){
    if (!src)
        return object;
    for (var p in src) {//遍历 src,并将所有属性赋值给object，从而实现对象的继承
        object[p] = src[p];
    }
    return object;
}

/*
 * $A:
 * 		遍历数据A，并把A中的参数入栈
 * params:
 *      o:数组
 * return:
 *      rt:栈
 */
var $A = function(o){
    var rt = [];
    for (var i = 0, j = o.length; i < j; i++) {
        rt.push(o[i]);
    }
    return rt;
};