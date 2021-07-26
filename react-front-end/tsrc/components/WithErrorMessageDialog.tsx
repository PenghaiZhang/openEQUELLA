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
import { useState } from "react";
import * as React from "react";
import MessageDialog from "./MessageDialog";

export interface WithErrorMessageDialogProps {
  /**
   * Update the error messages displayed in the dialog.
   */
  setMessages: (messages: string[]) => void;
}

/**
 * This HOC function returns a component that consists of the provided component and 'MessageDialog'
 * which is used to display error messages.
 */
export const withErrorMessageDialog =
  <ComponentProps,>(
    Component: React.ComponentType<ComponentProps & WithErrorMessageDialogProps>
  ) =>
  (props: ComponentProps) => {
    const [messages, setMessages] = useState<string[] | undefined>(undefined);
    return (
      <>
        <Component {...props} setMessages={setMessages} />
        {messages && (
          <MessageDialog
            open={messages.length > 0}
            messages={messages}
            title="Error"
            close={() => setMessages(undefined)}
          />
        )}
      </>
    );
  };
