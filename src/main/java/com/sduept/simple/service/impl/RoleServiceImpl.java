package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Role;
import com.sduept.simple.entity.User;
import com.sduept.simple.mapper.RoleMapper;
import com.sduept.simple.service.RoleService;
import com.sduept.simple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-21
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserService userService;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    TransactionDefinition transactionDefinition;

    @Autowired
    QueueHandler queueHandler;

    @Override
    public ServerResponse listRoles() {
        for (int i = 0; i< 10; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            queueHandler.reciveMsg("xiao" + i);
        }
        return ServerResponse.createBySuccess(list());
    }

    @Override
    public ServerResponse addRole(Role role) {
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        boolean isSuccessed = save(role);
        Role xiao = getOne(new QueryWrapper<Role>().lambda().eq(Role::getRoleName, "xiao"));
        User user = new User();
        user.setUserName("dsfdsfs");
        dataSourceTransactionManager.commit(transactionStatus);
        dataSourceTransactionManager.rollback(transactionStatus);
        userService.addUser(user);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("角色新增成功");
        }
        return ServerResponse.createByErrorMessage("角色新增失败");
    }

    @Override
    public ServerResponse editRole(Role role) {
        boolean isSuccessed = updateById(role);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("角色修改成功");
        }
        return ServerResponse.createByErrorMessage("角色修改失败");
    }

    @Override
    public ServerResponse deleteRole(Integer id) {
        if (Objects.isNull(id)) {
            return ServerResponse.createByErrorMessage("ID不能空");
        }
        boolean isSuccessed = removeById(id);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("角色删除成功");
        }
        return ServerResponse.createByErrorMessage("角色删除失败");
    }
}
