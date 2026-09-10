// SPDX-FileCopyrightText: 2025-2026 Social Connect Labs, Inc.
// SPDX-License-Identifier: BUSL-1.1
// NOTE: Converts to Apache-2.0 on 2029-06-11 per LICENSE.

// Regression test: fork/CI builds ship without app/.env, so Babel's
// react-native-dotenv injects `KYC_TEE_URL` as undefined. Session creation
// must fall back to the production TEE instead of fetching `undefined/session`.
import { createKycSession } from '@/integrations/kyc/kycService';

jest.mock('@env', () => ({}));

jest.mock('@didit-protocol/sdk-react-native', () => ({
  startVerification: jest.fn(),
}));

describe('createKycSession env fallback', () => {
  let fetchSpy: jest.SpyInstance;

  beforeEach(() => {
    fetchSpy = jest.spyOn(global, 'fetch').mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ sessionId: 'sid', sessionToken: 'tok' }),
    } as unknown as Response);
  });

  afterEach(() => {
    fetchSpy.mockRestore();
  });

  it('uses production TEE URL when KYC_TEE_URL is undefined', async () => {
    await createKycSession({ country: 'IND', nationality: 'IND' });
    expect(fetchSpy).toHaveBeenCalledWith(
      'https://kyc.self.xyz/session',
      expect.anything(),
    );
  });
});
