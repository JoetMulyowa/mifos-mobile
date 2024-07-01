package org.mifos.mobile.core.data.repositories

import org.mifos.mobile.core.network.DataManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import org.mifos.mobile.core.model.entity.accounts.savings.SavingsAccountApplicationPayload
import org.mifos.mobile.core.model.entity.accounts.savings.SavingsAccountUpdatePayload
import org.mifos.mobile.core.model.entity.accounts.savings.SavingsAccountWithdrawPayload
import org.mifos.mobile.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobile.core.model.entity.templates.account.AccountOptionsTemplate
import org.mifos.mobile.core.model.entity.templates.savings.SavingsAccountTemplate
import javax.inject.Inject

class SavingsAccountRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    SavingsAccountRepository {

    override suspend fun getSavingsWithAssociations(
        accountId: Long?,
        associationType: String?
    ): Flow<SavingsWithAssociations> {
        return flow {
            emit(dataManager.getSavingsWithAssociations(accountId, associationType))
        }
    }

    override suspend fun getSavingAccountApplicationTemplate(clientId: Long?): Flow<SavingsAccountTemplate> {
        return flow {
            emit(dataManager.getSavingAccountApplicationTemplate(clientId))
        }
    }

    override suspend fun submitSavingAccountApplication(payload: SavingsAccountApplicationPayload?): Flow<ResponseBody> {
        return flow {
            emit(dataManager.submitSavingAccountApplication(payload))
        }
    }

    override suspend fun updateSavingsAccount(
        accountId: Long?,
        payload: SavingsAccountUpdatePayload?
    ): Flow<ResponseBody> {
        return flow {
            emit(dataManager.updateSavingsAccount(accountId, payload))
        }
    }

    override suspend fun submitWithdrawSavingsAccount(
        accountId: String?,
        payload: SavingsAccountWithdrawPayload?
    ): Flow<ResponseBody> {
        return flow {
            emit(dataManager.submitWithdrawSavingsAccount(accountId, payload))
        }
    }

    override fun accountTransferTemplate(accountId: Long?, accountType: Long?): Flow<AccountOptionsTemplate> {
        return flow {
            emit(dataManager.accountTransferTemplate(accountId = accountType, accountType = accountType))
        }
    }
}