// SPDX-FileCopyrightText: 2025-2026 Social Connect Labs, Inc.
// SPDX-License-Identifier: BUSL-1.1
// NOTE: Converts to Apache-2.0 on 2029-06-11 per LICENSE.

export const KYC_PROVIDER = 'didit';

// KYC flow enabled for fork build
export const KYC_FLOW_ENABLED = true;

export const isKycFlowEnabled = (): boolean => KYC_FLOW_ENABLED;
