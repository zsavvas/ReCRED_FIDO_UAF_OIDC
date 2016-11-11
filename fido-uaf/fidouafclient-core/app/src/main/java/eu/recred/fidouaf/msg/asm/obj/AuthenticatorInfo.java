/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.recred.fidouaf.msg.asm.obj;

import eu.recred.fidouaf.msg.DisplayPNGCharacteristicsDescriptor;
import eu.recred.fidouaf.msg.Version;

public class AuthenticatorInfo {
	
	public int authenticatorIndex;
	public Version[] asmVersions;
	public boolean isUserEnrolled;
	public boolean hasSettings;
	public String aaid;
	public String assertionScheme;
	public short authenticationAlgorithm;
	public short[] attestationTypes;
	public long userVerification;
	public short keyProtection;
	public short matcherProtection;
	public long attachmentHint;
	public boolean isSecondFactorOnly;
	public boolean isRoamingAuthenticator;
	public String[] supportedExtensionIDs;
	public short tcDisplay;
	public String tcDisplayContentType;
	public DisplayPNGCharacteristicsDescriptor[] tcDisplayPNGCharacteristics;
	public String title;
	public String description;
	public String icon;

}
