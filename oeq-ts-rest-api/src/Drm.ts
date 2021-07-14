/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0, (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { is } from 'typescript-is';
import { GET, POST } from './AxiosInstance';

export interface DrmParties {
  /**  Server side language string for DRM party. */
  title: string;
  /** A list of text consisting each party's name and email. */
  partyList: string[];
}

export interface DrmCustomTerms {
  /** Server side language string for DRM terms. */
  title: string;
  /** Terms of using the Item. */
  terms: string;
}

export interface DrmAgreements {
  /** Text describing what regular permissions are granted to the user. */
  regularPermission?: string;
  /** Text describing what additional permissions are granted to the user. */
  additionalPermission?: string;
  /** Text describing that the use of Item is limited to education sector. */
  educationSector?: string;
  /** Text describing parties related to the Item. */
  parties?: DrmParties;
  /** Other terms and conditions applied to the Item. */
  customTerms?: DrmCustomTerms;
}

export interface ItemDrmDetails {
  /** Server side language string used as the DRM acceptance title. */
  title: string;
  /** Server side language string used as the DRM acceptance subtitle. */
  subtitle: string;
  /** Server side language string used as the DRM acceptance description. */
  description: string;
  /** All terms and conditions that user must accept to use the Item. */
  agreements: DrmAgreements;
}

const buildPath = (apiBasePath: string, uuid: string, version: number) =>
  `${apiBasePath}/item/${uuid}/${version}/drm`;

/**
 * List all of an Item's DRM terms.
 *
 * @param apiBasePath Base URI to the oEQ institution and API
 * @param uuid UUID of an Item
 * @param version Version of an Item
 */
export const listDrmTerms = (
  apiBasePath: string,
  uuid: string,
  version: number
): Promise<ItemDrmDetails> =>
  GET(buildPath(apiBasePath, uuid, version), (data): data is ItemDrmDetails =>
    is<ItemDrmDetails>(data)
  );

/**
 * Accept an Item's DRM terms.
 *
 * @param apiBasePath Base URI to the oEQ institution and API
 * @param uuid UUID of an Item
 * @param version Version of an Item
 */
export const acceptDrmTerms = (
  apiBasePath: string,
  uuid: string,
  version: number
): Promise<number> =>
  POST(buildPath(apiBasePath, uuid, version), (data): data is number =>
    is<number>(data)
  );