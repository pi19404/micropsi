//----------------------------------------------------------------------------
// Water Shader, dx7 version (no pixel shader)
//----------------------------------------------------------------------------

texture c_tDiffuseMap;
texture c_tEnvironmentMap;

// Matrices
float4x4    c_mWorld			: WORLD;
float4x4    c_mWorldViewProj	: WORLDVIEWPROJECTION;
float4x4	c_mViewProj			: VIEWPROJECTION;

//----------------------------------------------------------------------------
struct VSInput
{
    float3  vPos             : POSITION;
    float3  vNormal          : NORMAL;
    float3  vTex0            : TEXCOORD0;
};

struct VSOutput
{
    float4  vPos     : POSITION;
    float4	vColor	 : COLOR0;
    float4  vTex0    : TEXCOORD0;
};

//----------------------------------------------------------------------------
VSOutput 
VShade(VSInput i)
{
    VSOutput   o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);

	o.vColor = float4(0.4f, 0.4f, 0.6f, 1.0f);

	float4 f = mul(float4(i.vPos.x, 0.0f, i.vPos.z, 1.0f), c_mWorldViewProj);

	o.vTex0.x = f.x / 2.0f + (0.5f * f.w);
	o.vTex0.y = f.y / 2.0f + (0.5f * f.w);
	o.vTex0.z = f.z;
	o.vTex0.w = f.w;

    return o;
}

//----------------------------------------------------------------------------
sampler WaterDiffSampler = sampler_state
{
	texture   = <c_tDiffuseMap>;
	magfilter = LINEAR;
	minfilter = LINEAR;
	mipfilter = LINEAR;
	addressu  = WRAP;
	addressv  = WRAP; 
};

sampler WaterReflectionSampler = sampler_state
{
	texture	  = <c_tEnvironmentMap>;
	magfilter = LINEAR;
	minfilter = LINEAR;
	mipfilter = LINEAR;
	addressu  = WRAP;
	addressv  = WRAP; 
};

//----------------------------------------------------------------------------
VertexShader vsShader = compile vs_1_1 VShade();

//----------------------------------------------------------------------------
technique standard
{
    pass p0
    {
        VertexShader		= (vsShader);
        PixelShader			= 0;

		AlphaTestEnable		= false;
		
		ZEnable				= true;
		ZWriteEnable		= true;
		
		AlphaBlendEnable	= true;
		SrcBlend			= SRCALPHA;
		DestBlend			= INVSRCALPHA;
		
		TextureFactor		= 0xAFFFFFFF;
		
		CullMode			= CW;

        ColorOp[0]			= Modulate;
        ColorArg1[0]		= Texture;
        ColorArg2[0]		= Diffuse;

        ColorOp[1]			= Disable;
        
        AlphaOp[0]			= SelectArg1;
        AlphaArg1[0]		= TFactor;

        AlphaOp[1]			= Disable;

		Sampler[0]			= <WaterReflectionSampler>;
		Sampler[1]          = <WaterDiffSampler>;

		TextureTransformFlags[0] = projected; 
    }

    pass restore
    {
		TextureTransformFlags[0] = disable; 
    }

}

