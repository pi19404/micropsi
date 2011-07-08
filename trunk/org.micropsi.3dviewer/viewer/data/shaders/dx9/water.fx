//----------------------------------------------------------------------------
// Water Shader, dx9 version
//----------------------------------------------------------------------------

float4	c_vLightDir = {0.59f, -0.59f, 0.59f, 1.0f};
float   c_fAnimationSpeed = 0.02f;
float	c_fMinOpacity = 0.5f;

float	c_fTime;
float4	c_vEyePosition;

texture c_tDiffuseMap;
texture c_tBumpMap;
texture c_tNormalMap;
texture c_tEnvironmentMap;

// Matrices
float4x4    c_mWorld			: WORLD;
float4x4    c_mWorldViewProj	: WORLDVIEWPROJECTION;

//----------------------------------------------------------------------------
struct VSInput
{
    float3  vPos             : POSITION;
    float3  vNormal          : NORMAL;
    float3  vTex0            : TEXCOORD0;
};

struct VSOutput
{
    float4  vPos			: POSITION;
    float3  vTex			: TEXCOORD0;
	float3  vTexAnimated	: TEXCOORD1;
    float4  vTexProj		: TEXCOORD2;
    float3	vViewer			: TEXCOORD3;
    float3  vNormal			: TEXCOORD4;
};

struct PSOutput
{
	float4 color : COLOR0;
};

//----------------------------------------------------------------------------
VSOutput 
VShade(VSInput i)
{
    VSOutput o;
     
	float4 vTransformedPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);
	o.vPos = vTransformedPos;

	float3 vNormal = i.vNormal;
	o.vNormal = vNormal;
	o.vViewer = c_vEyePosition - mul(float4(i.vPos.xyz, 1.0f), c_mWorld);

	o.vTex = i.vTex0;
	
	float fTimeFrac = frac(c_fTime * c_fAnimationSpeed);
	o.vTexAnimated = i.vTex0 + fTimeFrac;

	o.vTexProj.x = vTransformedPos.x / 2.0f + (0.5f * vTransformedPos.w);
	o.vTexProj.y = vTransformedPos.y / 2.0f + (0.5f * vTransformedPos.w);
	o.vTexProj.z = vTransformedPos.z;
	o.vTexProj.w = vTransformedPos.w;

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
	addressu  = CLAMP;
	addressv  = CLAMP; 
};

sampler WaterBumpSampler = sampler_state
{
	texture   = <c_tBumpMap>;
	magfilter = LINEAR;
	minfilter = LINEAR;
	mipfilter = LINEAR;
	addressu  = WRAP;
	addressv  = WRAP; 
};

sampler WaterBumpSampler2 = sampler_state
{
	texture   = <c_tNormalMap>;
	magfilter = LINEAR;
	minfilter = LINEAR;
	mipfilter = LINEAR;
	addressu  = WRAP;
	addressv  = WRAP; 
};

//----------------------------------------------------------------------------
PSOutput
PShade(VSOutput i)
{
	PSOutput o;
	
	// calculate bumpmap normal from our two bumpmaps
	
	float3 bumpNormal1 = 2 * (tex2D(WaterBumpSampler, i.vTexAnimated) - 0.5); 
	float3 bumpNormal2 = 2 * (tex2D(WaterBumpSampler, i.vTex) - 0.5); 
	float3 bumpNormal = lerp(bumpNormal1, bumpNormal2, 0.5);

	// texture lookup: reflection map, diffuse map

	float4 vTexProjScrambled = i.vTexProj;
	vTexProjScrambled.xy += (0.8f * bumpNormal.xy);
	float4 reflectionColor = tex2Dproj(WaterReflectionSampler, vTexProjScrambled);

	float3 vTexScrambled = i.vTex + (0.2f * bumpNormal);
	float4 diffuseColor = tex2D(WaterDiffSampler, vTexScrambled );

	// lighting, color mixing
	
	float3 vViewer = normalize(i.vViewer);
	float3 vSurfaceNormal = normalize(i.vNormal);
	float3 reflectionVector = normalize(2 * saturate(dot(bumpNormal, c_vLightDir)) * bumpNormal - c_vLightDir);  
	
	float4 specular = pow(saturate(dot(reflectionVector, vViewer)), 128);
	
	float4 color = reflectionColor * diffuseColor + specular ;
	
	// calculate transparency
	
	float  f = pow(saturate(dot(vSurfaceNormal, vViewer)), 0.8);
	color.a = max(1 - f, c_fMinOpacity);

	o.color = color;
	return o;
}

//----------------------------------------------------------------------------
VertexShader vShader	= compile vs_1_1 VShade();
PixelShader pShader		= compile ps_2_0 PShade();

//----------------------------------------------------------------------------
// Techniques specs follow
//----------------------------------------------------------------------------
technique standard
{
    pass p0
    {
        VertexShader = (vShader);
        PixelShader  = (pShader);

		AlphaTestEnable = false;
		
		ZEnable = true;
		ZWriteEnable = true;
		
		AlphaBlendEnable = true;
		SrcBlend		 = SRCALPHA;
	    DestBlend		 = INVSRCALPHA;
		
		CullMode = CW;
    }
}

