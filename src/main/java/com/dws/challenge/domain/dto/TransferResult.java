package com.dws.challenge.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferResult {

    private String accountFromId;

    private BigDecimal balanceAfterTransfer;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountFromId == null) ? 0 : accountFromId.hashCode());
        result = prime * result + ((balanceAfterTransfer == null) ? 0 : balanceAfterTransfer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransferResult other = (TransferResult) obj;
        if (accountFromId == null) {
            if (other.accountFromId != null)
                return false;
        } else if (!accountFromId.equals(other.accountFromId))
            return false;
        if (balanceAfterTransfer == null) {
            if (other.balanceAfterTransfer != null)
                return false;
        } else if (!balanceAfterTransfer.equals(other.balanceAfterTransfer))
            return false;
        return true;
    }

}
