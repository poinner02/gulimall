package com.merchen.gulimall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName undo_log
 */
@TableName(value ="undo_log")
@Data
public class UndoLog implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long branchId;

    /**
     * 
     */
    private String xid;

    /**
     * 
     */
    private String context;

    /**
     * 
     */
    private Integer logStatus;

    /**
     * 
     */
    private Date logCreated;

    /**
     * 
     */
    private Date logModified;

    /**
     * 
     */
    private String ext;

    /**
     * 
     */
    private byte[] rollbackInfo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UndoLog other = (UndoLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBranchId() == null ? other.getBranchId() == null : this.getBranchId().equals(other.getBranchId()))
            && (this.getXid() == null ? other.getXid() == null : this.getXid().equals(other.getXid()))
            && (this.getContext() == null ? other.getContext() == null : this.getContext().equals(other.getContext()))
            && (this.getLogStatus() == null ? other.getLogStatus() == null : this.getLogStatus().equals(other.getLogStatus()))
            && (this.getLogCreated() == null ? other.getLogCreated() == null : this.getLogCreated().equals(other.getLogCreated()))
            && (this.getLogModified() == null ? other.getLogModified() == null : this.getLogModified().equals(other.getLogModified()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()))
            && (Arrays.equals(this.getRollbackInfo(), other.getRollbackInfo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBranchId() == null) ? 0 : getBranchId().hashCode());
        result = prime * result + ((getXid() == null) ? 0 : getXid().hashCode());
        result = prime * result + ((getContext() == null) ? 0 : getContext().hashCode());
        result = prime * result + ((getLogStatus() == null) ? 0 : getLogStatus().hashCode());
        result = prime * result + ((getLogCreated() == null) ? 0 : getLogCreated().hashCode());
        result = prime * result + ((getLogModified() == null) ? 0 : getLogModified().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        result = prime * result + (Arrays.hashCode(getRollbackInfo()));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", branchId=").append(branchId);
        sb.append(", xid=").append(xid);
        sb.append(", context=").append(context);
        sb.append(", logStatus=").append(logStatus);
        sb.append(", logCreated=").append(logCreated);
        sb.append(", logModified=").append(logModified);
        sb.append(", ext=").append(ext);
        sb.append(", rollbackInfo=").append(rollbackInfo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}