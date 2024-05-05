package org.mifos.mobile.ui.guarantor_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.mifos.mobile.R
import org.mifos.mobile.core.ui.component.MifosTitleDescDoubleLine
import org.mifos.mobile.models.guarantor.GuarantorPayload
import org.mifos.mobile.utils.DateHelper

@Composable
fun GuarantorDetailContent(
    data: GuarantorPayload
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        MifosTitleDescDoubleLine(
            title = stringResource(id = R.string.first_name),
            description = data.firstname ?: "",
            descriptionStyle =  MaterialTheme.typography.bodyLarge
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        MifosTitleDescDoubleLine(
            title = stringResource(id = R.string.last_name),
            description = data.lastname ?: "",
            descriptionStyle =  MaterialTheme.typography.bodyLarge
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        MifosTitleDescDoubleLine(
            title = stringResource(id = R.string.office_name),
            description = data.officeName ?: "",
            descriptionStyle =  MaterialTheme.typography.bodyLarge
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        MifosTitleDescDoubleLine(
            title = stringResource(id = R.string.guarantor_type),
            description = data.guarantorType?.value ?: "",
            descriptionStyle =  MaterialTheme.typography.bodyLarge
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        MifosTitleDescDoubleLine(
            title = stringResource(id = R.string.joined_date),
            description = DateHelper.getDateAsString(data.joinedDate),
            descriptionStyle =  MaterialTheme.typography.bodyLarge
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

    }
}