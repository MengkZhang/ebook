package com.tzpt.cloundlibrary.manager.base;

/**
 * 响应code
 * Created by ZhiqiangJia on 2017-10-26.
 */
public interface BaseResponseCode {
    int ERROR_CODE_UNKNOWN = -1;
    int ERROR_CODE_PARSE = -2;
    int ERROR_CODE_NETWORK = -3;
    int ERROR_CODE_HTTP = -4;

    //response code
    int CODE_SUCCESS = 200;             //响应成功
    int CODE_SERVICE_ERROR = 500;       //服务器内部异常

    //errorCode
    //login
    int ERROR_CODE_1001 = 1001;         //馆不存在
    int ERROR_CODE_1002 = 1002;         //用户不存在
    int ERROR_CODE_1003 = 1003;         //密码错误
    int ERROR_CODE_1004 = 1004;         //内部异常导致登陆失败
    int ERROR_CODE_1014 = 1014;         //用户无效
    int ERROR_CODE_1017 = 1017;         //用户不是第一次登录

    //获取管理员登录信息
    int ERROR_CODE_KICK_OUT = 1005;     //无法通过token获取用户信息，踢下线
    int ERROR_CODE_1006 = 1006;         //操作超时

    int ERROR_CODE_1008 = 1008;         //修改失败
    //开放时间新增修改
    int ERROR_CODE_3001 = 3001;           //修改失败
    int ERROR_CODE_3002 = 3002;           //新增失败
    //读者修改密码
    int ERROR_CODE_3103 = 3103;           //密码错误
    int ERROR_CODE_3108 = 3108;           //未注册
    int ERROR_CODE_3111 = 3111;           //非本馆读者
    int ERROR_CODE_3112 = 3112;           //该读者为限注册馆读者，
    int ERROR_CODE_3113 = 3113;           //非身份证读者在无押金馆需携带身份证借书
    int ERROR_CODE_3114 = 3114;           //非身份证注册读者第一次扫描
    int ERROR_CODE_3115 = 3115;           //请完成身份证刷证注册
    //发送短信
    int ERROR_CODE_3201 = 3201;           //手机号码已经注册
    int ERROR_CODE_3202 = 3202;           //手机号码操作频繁
    int ERROR_CODE_3101 = 3101;           //修改失败
    int ERROR_CODE_3200 = 3200;           //发送验证码失败
    //流通管理
    int ERROR_CODE_2408 = 2408;           //不可流通
    int ERROR_CODE_2410 = 2410;           //该流通单号下的条形码不存在
    int ERROR_CODE_2405 = 2405;           //删除流通书籍失败
    int ERROR_CODE_2206 = 2206;           //书籍无库位
    int ERROR_CODE_2207 = 2207;           //图书不存在
    int ERROR_CODE_2208 = 2208;           //书籍已在借
    int ERROR_CODE_2209 = 2209;           //书籍已亏盘
    int ERROR_CODE_2210 = 2210;           //书籍已流出
    int ERROR_CODE_2211 = 2211;           //书籍已丢失
    int ERROR_CODE_2212 = 2212;           //书籍已剔旧
    int ERROR_CODE_2214 = 2214;           //书籍已被其他人预约
    int ERROR_CODE_2218 = 2218;           //书籍已售
    int ERROR_CODE_2219 = 2219;           //未办借
    int ERROR_CODE_2220 = 2220;           //书籍已赔
    int ERROR_CODE_2006 = 2006;           //书籍无库房
    int ERROR_CODE_2402 = 2402;           //书籍在限制库或基藏库,无法流通
    int ERROR_CODE_2403 = 2403;           //流通记录不存在,无法流通
    int ERROR_CODE_2404 = 2404;           //流通记录不是待发送状态,无法继续新增流通书籍
    int ERROR_CODE_2400 = 2400;           //新增流通书籍失败
    int ERROR_CODE_2406 = 2406;           //删除流通记录失败

    //借书管理
    int ERROR_CODE_3400 = 3400;           //读者余额不足
    int ERROR_CODE_2305 = 2305;           //存在不能借阅的书籍
    int ERROR_CODE_2303 = 2303;           //图书馆账户不存在
    int ERROR_CODE_2307 = 2307;           //批量新增借阅记录失败
    int ERROR_CODE_2313 = 2313;           //超出最大借阅数
    int ERROR_CODE_2213 = 2213;           //读者账户不存在
    int ERROR_CODE_2215 = 2215;           //书籍是基藏库
    int ERROR_CODE_1015 = 1015;           //本馆已停用


    //还书管理
    int ERROR_CODE_2217 = 2217;           //书籍已在馆
    int ERROR_CODE_2309 = 2309;           //限制库不可异馆还书
    int ERROR_CODE_2308 = 2308;           //在借记录不存在
    int ERROR_CODE_2315 = 2315;           //借书馆和还书馆协议不同
    int ERROR_CODE_2316 = 2316;           //无协议下不可异馆还书
    int ERROR_CODE_2304 = 2304;           //读者账户不存在
    int ERROR_CODE_2317 = 2317;           //读者有未处理罚金

    //赔书管理
    int ERROR_CODE_2311 = 2311;            //存在本协议下不可赔偿的书籍
    int ERROR_CODE_2312 = 2312;            //批量更新借阅记录和书籍状态失败
    //注册
    int ERROR_CODE_3104 = 3104;            //手机号码错误
    int ERROR_CODE_3105 = 3105;            //批量更新借阅记录和书籍状态失败
    int ERROR_CODE_3106 = 3106;            //读者新增失败
    int ERROR_CODE_3110 = 3110;            //添加读者ACCOUNT失败

    //提现
    int ERROR_CODE_2500 = 2500;             //当月提现次数达上限
    int ERROR_CODE_2501 = 2501;             //最近两个月提现次数达上限
    int ERROR_CODE_2502 = 2502;             //当年提现次数达上限
    int ERROR_CODE_2503 = 2503;             //本次提现金额超限值

    //充值
    int ERROR_CODE_6100 = 6100;             //金额必须大于或则等于1元
    int ERROR_CODE_6101 = 6101;             //金额太大
    int ERROR_CODE_6102 = 6102;             //微信预支付订单第三方生成失败，稍后重试
    int ERROR_CODE_6105 = 6105;             //没有充值记录
    int ERROR_CODE_6106 = 6106;             //微信提现限制提醒
    int ERROR_CODE_7020 = 7020;             //图书馆信息不存在
    int ERROR_CODE_7021 = 7021;             //图书馆用户信息不存在
    int ERROR_CODE_7022 = 7022;             //图书馆为无协议，不可缴费
    int ERROR_CODE_8000 = 8000;             //订单未找到
    int ERROR_CODE_8001 = 8001;             //订单第三方查询异常
    int ERROR_CODE_6112 = 6112;             //交押金金额超限值 目的：在于限制每个读者只能有1000以内的最大可用押金
    int ERROR_CODE_6113 = 6113;             //请联系admin管理员完善退款支付宝账户信息

    //客户端异常码
    int ERROR_CODE_10000 = 10000;                   //超限值
    int ERROR_CODE_10001 = ERROR_CODE_10000 + 1;    //读者登录异常
    int ERROR_CODE_10002 = ERROR_CODE_10001 + 1;    //押金不足
    int ERROR_CODE_10003 = ERROR_CODE_10002 + 1;    //重复录入
    int ERROR_CODE_10004 = ERROR_CODE_10003 + 1;    //只能使用共享押金借书
    int ERROR_CODE_10005 = ERROR_CODE_10004 + 1;    //暂无赔书信息
    int ERROR_CODE_10006 = ERROR_CODE_10005 + 1;    //密码为空异常
    int ERROR_CODE_10007 = ERROR_CODE_10006 + 1;    //用户第一次登录异常码

}
