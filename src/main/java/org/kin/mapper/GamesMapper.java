package org.kin.mapper;

import org.kin.pojo.Games;

import java.util.List;

/**
 * Mapper接口
 */
public interface GamesMapper {

    //根据id查询数据
    Games findGamesById(int id);

    //查询所有数据
    List<Games> findAllGames();

}