package com.demo.inetty.tomcat;

/**
 * Description: 自定义servlet
 *
 * @author Zeti
 * @date 2021/3/19 下午2:27
 */
public abstract class ZServlet {

    protected abstract void doGet(ZRequest request, ZResponse response) throws Exception;

    protected abstract void doPost(ZRequest request, ZResponse response) throws Exception;

    public void service(ZRequest request, ZResponse response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

}
