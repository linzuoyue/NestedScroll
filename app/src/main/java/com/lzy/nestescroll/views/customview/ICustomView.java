package com.lzy.nestescroll.views.customview;

public interface ICustomView<S extends BaseCustomViewModel> {
    void setData(S data);
}
