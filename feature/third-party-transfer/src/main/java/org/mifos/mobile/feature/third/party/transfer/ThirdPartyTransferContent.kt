/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.third.party.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.mifos.mobile.core.designsystem.components.MifosButton
import org.mifos.mobile.core.designsystem.components.MifosOutlinedTextButton
import org.mifos.mobile.core.designsystem.components.MifosOutlinedTextField
import org.mifos.mobile.core.designsystem.theme.DarkGray
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.designsystem.theme.Primary
import org.mifos.mobile.core.model.entity.beneficiary.Beneficiary
import org.mifos.mobile.core.model.entity.payload.ReviewTransferPayload
import org.mifos.mobile.core.model.entity.templates.account.AccountOption
import org.mifos.mobile.core.ui.component.MFStepProcess
import org.mifos.mobile.core.ui.component.MifosDropDownDoubleTextField
import org.mifos.mobile.core.ui.component.StepProcessState
import org.mifos.mobile.core.ui.component.getStepState
import org.mifos.mobile.core.ui.utils.DevicePreviews

@Composable
internal fun ThirdPartyTransferContent(
    accountOption: List<AccountOption>,
    toAccountOption: List<AccountOption>,
    beneficiaryList: List<Beneficiary>,
    navigateBack: () -> Unit,
    addBeneficiary: () -> Unit,
    reviewTransfer: (ReviewTransferPayload) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    var payFromAccount by rememberSaveable { mutableStateOf<AccountOption?>(null) }
    var beneficiary by rememberSaveable { mutableStateOf<Beneficiary?>(null) }
    var amount by rememberSaveable { mutableStateOf("") }
    var remark by rememberSaveable { mutableStateOf("") }

    var currentStep by rememberSaveable { mutableIntStateOf(0) }

    val payFromStepState by remember {
        derivedStateOf { getStepState(targetStep = 0, currentStep = currentStep) }
    }

    val beneficiaryStepState by remember {
        derivedStateOf { getStepState(targetStep = 1, currentStep = currentStep) }
    }

    val amountStepState by remember {
        derivedStateOf { getStepState(targetStep = 2, currentStep = currentStep) }
    }

    val remarkStepState by remember {
        derivedStateOf { getStepState(targetStep = 3, currentStep = currentStep) }
    }

    val stepsState = listOf(
        Pair(payFromStepState, R.string.one),
        Pair(beneficiaryStepState, R.string.two),
        Pair(amountStepState, R.string.three),
        Pair(remarkStepState, R.string.four),
    )

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp)
            .fillMaxSize(),
    ) {
        for (step in stepsState) {
            MFStepProcess(
                stepNumber = stringResource(id = step.second),
                activateColor = Primary,
                processState = step.first,
                deactivateColor = DarkGray,
                isLastStep = step == stepsState.last(),
            ) { stepModifier ->
                when (step.second) {
                    R.string.one -> PayFromStep(
                        fromAccountOptions = accountOption,
                        processState = payFromStepState,
                        onContinueClick = {
                            payFromAccount = it
                            currentStep += 1
                        },
                        modifier = stepModifier,
                    )

                    R.string.two -> BeneficiaryStep(
                        beneficiaryList = beneficiaryList,
                        processState = beneficiaryStepState,
                        addBeneficiary = addBeneficiary,
                        onContinueClick = {
                            beneficiary = it
                            currentStep += 1
                        },
                        modifier = stepModifier,
                    )

                    R.string.three -> EnterAmountStep(
                        processState = amountStepState,
                        onContinueClick = {
                            amount = it
                            currentStep += 1
                        },
                        modifier = stepModifier,
                    )

                    R.string.four -> RemarkStep(
                        processState = remarkStepState,
                        onContinueClicked = {
                            remark = it
                            reviewTransfer(
                                ReviewTransferPayload(
                                    payFromAccount = payFromAccount!!,
                                    payToAccount = toAccountOption
                                        .firstOrNull { account ->
                                            account.accountNo == beneficiary?.accountNumber
                                        } ?: AccountOption(),
                                    amount = amount,
                                    review = remark,
                                ),
                            )
                        },
                        modifier = stepModifier,
                        onCancelledClicked = navigateBack,
                    )
                }
            }
        }
    }
}

@Composable
private fun PayFromStep(
    fromAccountOptions: List<AccountOption>,
    processState: StepProcessState,
    onContinueClick: (AccountOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var payFromAccount by rememberSaveable { mutableStateOf<AccountOption?>(null) }
    var payFromError by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.pay_from),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        if (processState == StepProcessState.ACTIVE) {
            MifosDropDownDoubleTextField(
                optionsList = fromAccountOptions
                    .filter { it.accountType?.value != context.getString(R.string.loan_type) }
                    .map { Pair(it.accountNo ?: "", it.accountType?.value ?: "") },
                selectedOption = payFromAccount?.accountNo ?: "",
                labelResId = R.string.select_pay_from,
                error = payFromError,
                supportingText = stringResource(id = R.string.required),
                onClick = { index, _ ->
                    payFromAccount = fromAccountOptions[index]
                    payFromError = false
                },
            )
            MifosButton(
                textResId = R.string.continue_str,
                onClick = {
                    if (payFromAccount == null) {
                        payFromError = true
                    } else {
                        onContinueClick(payFromAccount ?: AccountOption())
                    }
                },
            )
        }
    }
}

@Composable
private fun BeneficiaryStep(
    beneficiaryList: List<Beneficiary>,
    processState: StepProcessState,
    addBeneficiary: () -> Unit,
    onContinueClick: (Beneficiary) -> Unit,
    modifier: Modifier = Modifier,
) {
    var beneficiary by rememberSaveable { mutableStateOf<Beneficiary?>(null) }
    var beneficiaryError by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.beneficiary),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        if (processState == StepProcessState.ACTIVE) {
            if (beneficiaryList.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_beneficiary_found_please_add),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
                MifosButton(
                    onClick = { addBeneficiary() },
                    textResId = R.string.add_beneficiary,
                )
            } else {
                MifosDropDownDoubleTextField(
                    optionsList = beneficiaryList
                        .map { Pair(it.accountNumber ?: "", it.name ?: "") },
                    selectedOption = beneficiary?.accountNumber ?: "",
                    labelResId = R.string.select_pay_from,
                    error = beneficiaryError,
                    supportingText = stringResource(id = R.string.required),
                    onClick = { index, _ ->
                        beneficiary = beneficiaryList[index]
                        beneficiaryError = false
                    },
                )
                MifosButton(
                    onClick = {
                        if (beneficiary == null) {
                            beneficiaryError = true
                        } else {
                            onContinueClick(beneficiary!!)
                        }
                    },
                    textResId = R.string.continue_str,
                )
            }
        } else {
            Text(
                text = stringResource(id = R.string.select_beneficiary),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun EnterAmountStep(
    processState: StepProcessState,
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var amountError by rememberSaveable { mutableStateOf<Int?>(null) }
    var showAmountError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = amount) {
        showAmountError = false
        amountError = when {
            amount.text.trim() == "" -> R.string.enter_amount
            amount.text.toDoubleOrNull() == null -> R.string.invalid_amount
            amount.text.toDoubleOrNull() == 0.0 -> R.string.amount_greater_than_zero
            else -> null
        }
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.amount),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        if (processState == StepProcessState.ACTIVE) {
            MifosOutlinedTextField(
                modifier = Modifier,
                value = amount,
                keyboardType = KeyboardType.Number,
                onValueChange = { amount = it },
                error = showAmountError,
                supportingText = amountError?.let { stringResource(id = it) },
                label = R.string.enter_amount,
            )
            MifosButton(
                onClick = {
                    if (amountError == null) {
                        onContinueClick(amount.text)
                    } else {
                        showAmountError = true
                    }
                },
                textResId = R.string.continue_str,
            )
        } else {
            Text(
                text = stringResource(id = R.string.enter_amount),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun RemarkStep(
    processState: StepProcessState,
    onContinueClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCancelledClicked: () -> Unit = {},
) {
    var remark by remember { mutableStateOf(TextFieldValue("")) }
    var remarkError by rememberSaveable { mutableStateOf<Int?>(null) }
    var showRemarkError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = remark) {
        showRemarkError = false
        remarkError = when {
            remark.text.trim().isBlank() -> R.string.remark_is_mandatory
            else -> null
        }
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.remark),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        if (processState == StepProcessState.ACTIVE) {
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
                value = remark,
                isError = showRemarkError,
                supportingText = { remarkError?.let { stringResource(id = it) } },
                onValueChange = { remark = it },
                label = { Text(text = stringResource(id = R.string.remark)) },
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                MifosButton(
                    onClick = {
                        remarkError?.let { showRemarkError = true }
                            ?: onContinueClicked(remark.text)
                    },
                    textResId = R.string.review,
                )
                Spacer(modifier = Modifier.width(12.dp))
                MifosOutlinedTextButton(
                    onClick = { onCancelledClicked() },
                    textResId = R.string.cancel,
                )
            }
        } else {
            Text(
                text = stringResource(id = R.string.enter_remarks),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ThirdPartyTransferContentPreview() {
    MifosMobileTheme {
        ThirdPartyTransferContent(
            accountOption = listOf(),
            toAccountOption = listOf(),
            beneficiaryList = listOf(),
            navigateBack = {},
            addBeneficiary = {},
            reviewTransfer = {},
        )
    }
}