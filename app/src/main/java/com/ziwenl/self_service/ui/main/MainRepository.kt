package com.ziwenl.self_service.ui.main

import com.ziwenl.baselibrary.base.repository.BaseRepositoryBoth
import com.ziwenl.baselibrary.base.repository.ILocalDataSource
import com.ziwenl.baselibrary.base.repository.IRemoteDataSource
import javax.inject.Inject

/**
 * PackageName : com.ziwenl.self_service.ui.main
 * Author : Ziwen Lan
 * Date : 2020/7/8
 * Time : 15:16
 * Introduction :
 */
class MainRepository @Inject constructor(
    remoteDatasource: MainRemoteDataSource,
    localDataSource: MainLocalDataSource
) : BaseRepositoryBoth<MainRemoteDataSource, MainLocalDataSource>(remoteDatasource, localDataSource)


class MainRemoteDataSource @Inject constructor() : IRemoteDataSource

class MainLocalDataSource @Inject constructor() : ILocalDataSource