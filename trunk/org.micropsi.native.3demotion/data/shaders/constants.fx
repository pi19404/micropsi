
shared float4x4		c_mWorld2Shadow;

shared float3		c_vShadowBase;
shared float		c_fShadowFadeFactor;

shared float4		c_vShadowColor = {0, 0, 0, 0};
shared float		c_fLightFactor = 1;
shared bool			c_bZWriteZTest = true;

shared texture2D	c_tShadowMap;
shared texture2D	c_tToonShadowTexture;
shared texture2D	c_tShadowFadeTexture;

int		c_iCurNumBones = 2;

technique t0 {}