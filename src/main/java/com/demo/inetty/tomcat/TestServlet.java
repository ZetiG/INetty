package com.demo.inetty.tomcat;

/**
 * Description: 自测servlet
 *
 * @author Zeti
 * @date 2021/3/19 下午2:31
 */
public class TestServlet extends ZServlet {

    @Override
    protected void doGet(ZRequest request, ZResponse response) throws Exception {
        doPost(request, response);
    }

    @Override
    protected void doPost(ZRequest request, ZResponse response) throws Exception {
        response.write("This is FirstServlet");
    }
}
