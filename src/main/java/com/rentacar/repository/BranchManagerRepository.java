package com.rentacar.repository;

import com.rentacar.entity.BranchManager;
import com.rentacar.entity.BranchManagerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BranchManagerRepository extends JpaRepository<BranchManager, BranchManagerId> {
    List<BranchManager> findByBranch_BranchID(Integer branchId);
    List<BranchManager> findByManager_ManagerID(Integer managerId);
    List<BranchManager> findByManager_User_UserID(Integer userId);
    // İleride özel sorgular gerekirse buraya eklenebilir
    // Örneğin, bir şubeye atanmış tüm managerları veya bir managerın atandığı tüm şubeleri bulmak için.

    // YENİ EKLENEN METOT
    boolean existsByBranch_BranchID(Integer branchId);
} 