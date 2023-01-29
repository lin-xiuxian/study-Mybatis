package com.lxx.mybatis;

import com.lxx.mybatis.entity.Goods;
import com.lxx.mybatis.utils.MybatisUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBatisTestor {
    /**
     * 初始化SqlSessionFactory
     * @throws IOException
     */
    @Test
    public void testSqlSessionFactory() throws IOException {
        //利用Reader加载classpath下的mybatis-config.xml核心配置文件
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        //初始化SqlSessionFactory对象,同时解析mybatis-config.xml文件
        //SqlSessionFactory 对象用于构建 SqlSession 对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        System.out.println("SessionFactory success!");
        SqlSession sqlSession = null;
        try {
            //创建SqlSession对象,SqlSession是JDBC的扩展类,用于与数据库交互
            sqlSession = sqlSessionFactory.openSession();
            //创建数据库连接(测试用)
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            if(sqlSession != null){
                //如果type="POOLED",代表使用连接池,close则是将连接回收到连接池中
                //如果type="UNPOOLED",代表直连,close则会调用Connection.close()方法关闭连接
                sqlSession.close();
            }
        }
    }

    @Test
    public void testMybatisUtils(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtils.openSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(sqlSession);
        }
    }
    @Test
    public void testSelectAll(){
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            List<Goods> list =  session.selectList("goods.selectAll");
            for(Goods g: list){
                System.out.println(g.getTitle());
            }
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectById() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 1603);
            System.out.println(goods.getTitle());
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectByPriceRange() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Map<String, Integer> param = new HashMap<>();
            param.put("min", 100);
            param.put("max", 500);
            param.put("limit", 10);
            List<Goods> list = session.selectList("selectByPriceRange", param);
            for(Goods g: list){
                System.out.println(g.getTitle() + ":" + g.getCurrentPrice());
            }
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }
}
