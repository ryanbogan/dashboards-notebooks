/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
 
package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.model.GetNotebookRequest
import org.opensearch.notebooks.model.GetNotebookResponse
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.transport.TransportService

/**
 * Get notebook transport action
 */
internal class GetNotebookAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<GetNotebookRequest, GetNotebookResponse>(NAME,
    transportService,
    client,
    actionFilters,
    ::GetNotebookRequest) {
    companion object {
        private const val NAME = "cluster:admin/opendistro/notebooks/get"
        internal val ACTION_TYPE = ActionType(NAME, ::GetNotebookResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: GetNotebookRequest, user: User?): GetNotebookResponse {
        return NotebookActions.info(request, user)
    }
}
