/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.model.RestTag.ACCESS_LIST_FIELD
import org.opensearch.notebooks.model.RestTag.CREATED_TIME_FIELD
import org.opensearch.notebooks.model.RestTag.ID_FIELD
import org.opensearch.notebooks.model.RestTag.NOTEBOOK_FIELD
import org.opensearch.notebooks.model.RestTag.TENANT_FIELD
import org.opensearch.notebooks.model.RestTag.UPDATED_TIME_FIELD
import org.opensearch.notebooks.security.UserAccessManager.DEFAULT_TENANT
import org.opensearch.notebooks.util.logger
import org.opensearch.notebooks.util.stringList
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.ToXContent.EMPTY_PARAMS
import org.opensearch.common.xcontent.ToXContentObject
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParserUtils
import java.time.Instant

/**
 * Wrapper data class over Notebook to add metadata
 * <pre> JSON format
 * {@code
 * {
 *   "id":"id",
 *   "lastUpdatedTimeMs":1603506908773,
 *   "createdTimeMs":1603506908773,
 *   "tenant":"__user__",
 *   "access":["User:user", "Role:sample_role", "BERole:sample_backend_role"]
 *   "notebook":{
 *      // refer [org.opensearch.notebooks.model.Notebook]
 *   }
 * }
 * }</pre>
 */
internal data class NotebookDetails(
    val id: String,
    val updatedTime: Instant,
    val createdTime: Instant,
    val tenant: String,
    val access: List<String>,
    val notebook: Notebook
) : ToXContentObject {
    internal companion object {
        private val log by logger(NotebookDetails::class.java)

        /**
         * Parse the data from parser and create NotebookDetails object
         * @param parser data referenced at parser
         * @param useId use this id if not available in the json
         * @return created NotebookDetails object
         */
        fun parse(parser: XContentParser, useId: String? = null): NotebookDetails {
            var id: String? = useId
            var updatedTime: Instant? = null
            var createdTime: Instant? = null
            var tenant: String? = null
            var access: List<String> = listOf()
            var notebook: Notebook? = null
            XContentParserUtils.ensureExpectedToken(XContentParser.Token.START_OBJECT, parser.currentToken(), parser)
            while (XContentParser.Token.END_OBJECT != parser.nextToken()) {
                val fieldName = parser.currentName()
                parser.nextToken()
                when (fieldName) {
                    ID_FIELD -> id = parser.text()
                    UPDATED_TIME_FIELD -> updatedTime = Instant.ofEpochMilli(parser.longValue())
                    CREATED_TIME_FIELD -> createdTime = Instant.ofEpochMilli(parser.longValue())
                    TENANT_FIELD -> tenant = parser.text()
                    ACCESS_LIST_FIELD -> access = parser.stringList()
                    NOTEBOOK_FIELD -> notebook = Notebook.parse(parser)
                    else -> {
                        parser.skipChildren()
                        log.info("$LOG_PREFIX:NotebookDetails Skipping Unknown field $fieldName")
                    }
                }
            }
            id ?: throw IllegalArgumentException("$ID_FIELD field absent")
            updatedTime ?: throw IllegalArgumentException("$UPDATED_TIME_FIELD field absent")
            createdTime ?: throw IllegalArgumentException("$CREATED_TIME_FIELD field absent")
            tenant = tenant ?: DEFAULT_TENANT
            notebook ?: throw IllegalArgumentException("$NOTEBOOK_FIELD field absent")
            return NotebookDetails(
                id,
                updatedTime,
                createdTime,
                tenant,
                access,
                notebook
            )
        }
    }

    /**
     * create XContentBuilder from this object using [XContentFactory.jsonBuilder()]
     * @param params XContent parameters
     * @return created XContentBuilder object
     */
    fun toXContent(params: ToXContent.Params = EMPTY_PARAMS): XContentBuilder? {
        return toXContent(XContentFactory.jsonBuilder(), params)
    }

    /**
     * {ref toXContent}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        builder!!
        builder.startObject()
        if (params?.paramAsBoolean(ID_FIELD, false) == true) {
            builder.field(ID_FIELD, id)
        }
        builder.field(UPDATED_TIME_FIELD, updatedTime.toEpochMilli())
            .field(CREATED_TIME_FIELD, createdTime.toEpochMilli())
            .field(TENANT_FIELD, tenant)
        if (params?.paramAsBoolean(ACCESS_LIST_FIELD, true) == true && access.isNotEmpty()) {
            builder.field(ACCESS_LIST_FIELD, access)
        }
        builder.field(NOTEBOOK_FIELD)
        notebook.toXContent(builder, params)
        builder.endObject()
        return builder
    }
}
