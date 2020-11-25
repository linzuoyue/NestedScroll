package com.lzy.nestescroll.views.customview

interface ICustomView<S : BaseCustomViewModel> {
    fun setData(data: S)
}