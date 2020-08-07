package com.ziwenl.baselibrary.base.repository

/**
 * PackageName : com.ziwenl.baselibrary.base.repository
 * Author : Ziwen Lan
 * Date : 2020/7/2
 * Time : 11:23
 * Introduction :
 */
open class BaseRepositoryBoth<T : IRemoteDataSource, R : ILocalDataSource>(
    val remoteDataSource: T,
    val localDataSource: R
) : IRepository

open class BaseRepositoryLocal<T : ILocalDataSource>(
    val remoteDataSource: T
) : IRepository

open class BaseRepositoryRemote<T : IRemoteDataSource>(
    val remoteDataSource: T
) : IRepository

open class BaseRepositoryNothing() : IRepository