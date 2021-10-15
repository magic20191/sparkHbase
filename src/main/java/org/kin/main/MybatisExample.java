package org.kin.main;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.kin.mapper.GamesMapper;
import org.kin.mapper.SqlMapper;
import org.kin.pojo.Games;

/**
 * @description:
 * @author: Andy
 * @time: 2021/10/15 18:26
 */
public class MybatisExample {

    @org.junit.Test
    public void test() throws IOException {


        String resource = "mybatis-config.xml";
        InputStream resourceAsStream = Resources.getResourceAsStream(resource);
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
		/*
		SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。
		使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，
		多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。
		最简单的就是使用单例模式或者静态单例模式。
		 */
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
		/*
		每个线程都应该有它自己的 SqlSession 实例。
		SqlSession 的实例不是线程安全的，因此是不能被共享的每个线程都应该有它自己的 SqlSession 实例。
		SqlSession 的实例不是线程安全的，因此是不能被共享的
		 */
        SqlSession sqlSession = sqlSessionFactory.openSession();
        GamesMapper mapper = sqlSession.getMapper(GamesMapper.class);
//        int xiaoming = ;  // .addUser("xiaoming");
        List<Games> a = mapper.findAllGames();
        for(Games b:a){
            System.out.println(b.getId() +" " +b.getName());
        }

//        sqlSession.commit();//不用此句，数据库中是不会改变的
    }
}

