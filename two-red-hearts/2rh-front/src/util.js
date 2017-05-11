/**
 * getUrlParam:
 * https://github.com/GoogleChrome/ioweb2015/blob/21d7a80aefd6a76474fcdb700ac9965cd4c7800f/app/scripts/helper/util.js#L142-L200
 *
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const utils = {
  /**
   * Gets a param from the search part of a URL by name.
   * @param {string} param URL parameter to look for.
   * @return {string|undefined} undefined if the URL parameter does not exist.
   */
  getUrlParam(param) {
    if (!window.location.search) {
      return;
    }
    const m = new RegExp(param + '=([^&]*)').exec(window.location.search.substring(1));
    if (!m) {
      return;
    }
    return decodeURIComponent(m[1]);
  }
};

export default utils;
