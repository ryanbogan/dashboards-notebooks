/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { schema } from '@osd/config-schema';
import {
  IRouter,
  IOpenSearchDashboardsResponse,
  ResponseError,
  ILegacyScopedClusterClient,
} from '../../../../src/core/server';
import { API_PREFIX, wreckOptions } from '../../common';
import BACKEND from '../adaptors';

export function NoteRouter(router: IRouter) {
  // Fetch all the notebooks available
  router.get(
    {
      path: `${API_PREFIX}/`,
      validate: {},
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      let notebooksData = [];
      try {
        notebooksData = await BACKEND.viewNotes(opensearchNotebooksClient, wreckOptions);
        return response.ok({
          body: {
            data: notebooksData,
          },
        });
      } catch (error) {
        console.log('Notebook:', error);
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

  // Get all paragraphs of notebooks
  router.get(
    {
      path: `${API_PREFIX}/note/{noteId}`,
      validate: {
        params: schema.object({
          noteId: schema.string(),
        }),
      },
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      try {
        const notebookinfo = await BACKEND.fetchNote(opensearchNotebooksClient, request.params.noteId, wreckOptions);
        return response.ok({
          body: notebookinfo,
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

  // Add a Notebook
  router.post(
    {
      path: `${API_PREFIX}/note`,
      validate: {
        body: schema.object({
          name: schema.string(),
        }),
      },
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      try {
        const addResponse = await BACKEND.addNote(opensearchNotebooksClient, request.body, wreckOptions);
        return response.ok({
          body: addResponse.message.notebookId,
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

  // Rename a notebook
  router.put(
    {
      path: `${API_PREFIX}/note/rename`,
      validate: {
        body: schema.object({
          name: schema.string(),
          noteId: schema.string(),
        }),
      },
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      try {
        const renameResponse = await BACKEND.renameNote(opensearchNotebooksClient, request.body, wreckOptions);
        return response.ok({
          body: renameResponse,
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

  // Clone a notebook
  router.post(
    {
      path: `${API_PREFIX}/note/clone`,
      validate: {
        body: schema.object({
          name: schema.string(),
          noteId: schema.string(),
        }),
      },
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      try {
        const cloneResponse = await BACKEND.cloneNote(opensearchNotebooksClient, request.body, wreckOptions);
        return response.ok({
          body: cloneResponse,
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

  // Delete a notebook
  router.delete(
    {
      path: `${API_PREFIX}/note/{noteid}`,
      validate: {
        params: schema.object({
          noteid: schema.string(),
        }),
      },
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      try {
        const delResponse = await BACKEND.deleteNote(opensearchNotebooksClient, request.params.noteid, wreckOptions);
        return response.ok({
          body: delResponse,
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

  // Add sample notebooks
  router.post(
    {
      path: `${API_PREFIX}/note/addSampleNotebooks`,
      validate: {
        body: schema.object({
          visIds: schema.arrayOf(schema.string()),
        }),
      },
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const opensearchNotebooksClient: ILegacyScopedClusterClient = context.notebooks_plugin.opensearchNotebooksClient.asScoped(
        request
      );
      try {
        const addSampleNotesResponse = await BACKEND.addSampleNotes(
          opensearchNotebooksClient,
          request.body.visIds,
          wreckOptions
        );
        return response.ok({
          body: addSampleNotesResponse,
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );

}
