//----------------------------------------------------------------------------
// terrain-shader
//----------------------------------------------------------------------------

float4	c_vLightDir = {1.0f, -1.0f, 1.0f, 1.0f};

float4  c_vLightDiffuseColor = {1.0f, 1.0f, 1.0f, 1.0f};
float   c_fLightDiffuseIntensity = 0.6f;

float4  c_vLightAmbientColor = {1.0f, 1.0f, 1.0f, 1.0f};
float   c_fLightAmbientIntensity = 0.5f;

float4	c_fShadowColor = { 0.0f, 0.0f, 0.0f, 0.25f };

bool 	c_bBlending = false;				/// enables / disables alpha blending with blend map in splat pass
int 	c_iTFactor = 0xFFFFFFFF;			/// used to fade out splats in splat pass
int		c_iCullMode = 2;					/// CW

texture2D	c_tDiffuseMap;
texture2D	c_tDetailMap;

shared texture2D	c_tShadowMap;
shared texture2D	c_tShadowFadeTexture;

// Matrices
float4x3    c_mWorld			: WORLD;
float4x4    c_mWorldViewProj	: WORLDVIEWPROJECTION;

shared float4x4		c_mWorld2Shadow;

//----------------------------------------------------------------------------
struct VSInput
{
    float4  vPos             : POSITION;
    float3  vNormal			 : NORMAL;
    float2  vTex0            : TEXCOORD0;
    float2  vTex1            : TEXCOORD1;
};

//----------------------------------------------------------------------------
struct VSOutput
{
    float4  vPos     : POSITION;
    float4  vDiffuse : COLOR0;
    float2  vTex0    : TEXCOORD0;
    float2  vTex1    : TEXCOORD1;
};

//----------------------------------------------------------------------------
struct VSOutput_Shadow
{
    float4  vPos		: POSITION;
    float4  vDiffuse	: COLOR0;
    float4  vSpecular	: COLOR1;
    float2  vTex0		: TEXCOORD0;
    float2  vTex1		: TEXCOORD1;
    float2	vTex2		: TEXCOORD2;
};

//----------------------------------------------------------------------------
float4 CalculateLighting(float3 vWorldNormal)
{
	float3 vLightDiffuseColor = c_vLightDiffuseColor * max(0, dot(vWorldNormal, -c_vLightDir));
	float4 vLight;
	vLight.rgb = vLightDiffuseColor * c_fLightDiffuseIntensity + c_vLightAmbientColor * c_fLightAmbientIntensity;
    vLight.a   = 1.0f;
    return vLight;  
}

//----------------------------------------------------------------------------
// Vertex Shader for Splat Pass (no shadow)
VSOutput 
VSSplatPass(VSInput i)
{
    VSOutput   o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);

	float3 vNormal = normalize(mul(i.vNormal, c_mWorld));
    o.vDiffuse = CalculateLighting(vNormal);  

    o.vTex0  	= i.vTex0.xy;
    o.vTex1  	= i.vTex1.xy;

    return o;
}
//----------------------------------------------------------------------------
// Vertex Shader for Trivail Pass (with shadow)
VSOutput_Shadow
VSTrivialPassShadowed(VSInput i)
{
    VSOutput_Shadow  o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);

	float3 vNormal = normalize(mul(i.vNormal, c_mWorld));
    o.vDiffuse = CalculateLighting(vNormal);  
    o.vSpecular = c_fShadowColor;

    o.vTex0  	= i.vTex0.xy;
    o.vTex1  	= i.vTex1.xy;

   float3 vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorld);
   float4 tex	= mul(float4(vPos, 1.0f), c_mWorld2Shadow);

    o.vTex1 = tex.xy;
    o.vTex2 = tex.xy;

    return o;
}
//----------------------------------------------------------------------------
// Vertex Shader for Base Pass (no shadow)
VSOutput 
VSBasePass(VSInput i)
{
    VSOutput   o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);

	float3 vNormal = normalize(mul(i.vNormal, c_mWorld));
    o.vDiffuse = CalculateLighting(vNormal);  
    
    o.vTex0  	= i.vTex1.xy;
    o.vTex1  	= i.vTex1.xy;

    return o;
}
//----------------------------------------------------------------------------
// Vertex Shader for Base Pass with shadow
VSOutput_Shadow 
VSBasePassShadowed(VSInput i)
{
    VSOutput_Shadow o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);

	float3 vNormal = normalize(mul(i.vNormal, c_mWorld));
    o.vDiffuse = CalculateLighting(vNormal);  
    
    o.vSpecular = c_fShadowColor;
    
    float3 vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorld);
   
    o.vTex0  	= i.vTex1.xy;

    float4 tex	= mul(float4(vPos, 1.0f), c_mWorld2Shadow);

//    o.vTex1.x = tex.x / tex.w;
//    o.vTex1.y = tex.y / tex.w;

    o.vTex1 = tex.xy;
    o.vTex2 = tex.xy;

/*
    float4 f = mul(float4(vPos, 1.0f), c_mWorld2Shadow);

	o.vTex1.x = f.x / 2.0f + (0.5f * f.w);
	o.vTex1.y = f.y / 2.0f + (0.5f * f.w);
	o.vTex1.z = f.z;
	o.vTex1.w = f.w;
*/
    return o;
}

//----------------------------------------------------------------------------
// Vertex Shader for Shadow-only Pass
VSOutput_Shadow 
VSShadowPass(VSInput i)
{
    VSOutput_Shadow o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);

    // Shade (Ambient + etc.)
	float3 vNormal = normalize(mul(i.vNormal, c_mWorld));
    o.vDiffuse = CalculateLighting(vNormal);  
    
    o.vSpecular = c_fShadowColor;
    
    float3 vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorld);
   
    float4 tex	= mul(float4(vPos, 1.0f), c_mWorld2Shadow);

    o.vTex0 = tex.xy;
    o.vTex1 = tex.xy;
    o.vTex2 = tex.xy;

    return o;
}


//----------------------------------------------------------------------------
VertexShader vsSplatPass			= compile vs_1_1 VSSplatPass();
VertexShader vsBasePass				= compile vs_1_1 VSBasePass();
VertexShader vsBasePassShadowed		= compile vs_1_1 VSBasePassShadowed();
VertexShader vsShadowPass			= compile vs_1_1 VSShadowPass();
VertexShader vsTrivalPassShadowed	= compile vs_1_1 VSTrivialPassShadowed();

//----------------------------------------------------------------------------
// Techniques specs follow
//----------------------------------------------------------------------------
/**
	standard-technique: applies a splat pass; shadow is done in separate shadow pass
*/
technique standard
{
    pass p0
    {
        VertexShader = (vsSplatPass);
        PixelShader  = 0;

		AlphaTestEnable = false;

		ZEnable 		= true;
		ZWriteEnable 	= true;
        ZFunc           = LessEqual;		

        CullMode		= <c_iCullMode>;
		TextureFactor	= <c_iTFactor>;

        AlphaBlendEnable = <c_bBlending>;
        SrcBlend		 = SRCALPHA;
        DestBlend		 = INVSRCALPHA;

		Texture[0]		= (c_tDiffuseMap);

        ColorOp[0]   	= Modulate;
        ColorArg1[0] 	= Texture;
        ColorArg2[0] 	= Diffuse;
        
        AlphaOp[0] 	 	= SelectArg1;
        AlphaArg1[0] 	= Diffuse;

		Texture[1]		= (c_tDetailMap);
        
        ColorOp[1] 	 	= SelectArg1;
        ColorArg1[1] 	= Current;
        
        AlphaOp[1] 	 	= Modulate;
        AlphaArg1[1] 	= Texture;
        AlphaArg2[1]	= TFactor;

        ColorOp[2] 	 	= Disable;
        AlphaOp[2] 	 	= Disable;

		AddressU[0] 	= Wrap;
		AddressV[0] 	= Wrap;

		AddressU[1] 	= Clamp;
		AddressV[1] 	= Clamp;
	}
}

/**
	basepass: applies a base pass without shadow (used if splat passes and shadow pass follow)
*/
technique basepass
{
    pass p0
    {
        VertexShader = (vsBasePass);
        PixelShader  = 0;

		AlphaTestEnable = false;

		ZEnable 		= true;
		ZWriteEnable 	= true;
        ZFunc           = LessEqual;		

        CullMode  = <c_iCullMode>;

        AlphaBlendEnable = false;

		Texture[0]		= (c_tDiffuseMap);

        ColorOp[0] 	 	= Modulate;
        ColorArg1[0] 	= Texture;
        ColorArg2[0] 	= Diffuse;
        
        AlphaOp[0] 	 	= SelectArg1;
        AlphaArg1[0] 	= Texture;

        ColorOp[1] 	 	= Disable;
        AlphaOp[1] 	 	= Disable;

        ColorOp[2] 	 	= Disable;
        AlphaOp[2] 	 	= Disable;

        ColorOp[3] 	 	= Disable;
        AlphaOp[3] 	 	= Disable;
        
		AddressU[0] 	= Clamp;
		AddressV[0] 	= Clamp;
	}
}

/**
	basepass with shadow; used if basepass *only* is used (otherwise shadow is done is separate shadow pass)
*/
technique basepass_shadowed
{
    pass p0
    {
        VertexShader = (vsBasePassShadowed);
        PixelShader  = 0;

		AlphaTestEnable = false;

		ZEnable 		= true;
		ZWriteEnable 	= true;
        ZFunc           = LessEqual;		

        CullMode  = <c_iCullMode>;

		AlphaBlendEnable = true;
		SrcBlend		 = invsrcalpha;
		DestBlend		 = zero;

		Texture[0]		= (c_tDiffuseMap);

        ColorOp[0] 	 	= Modulate;
        ColorArg1[0] 	= Texture;
        ColorArg2[0] 	= Diffuse;
        
        AlphaOp[0] 	 	= SelectArg1;
        AlphaArg1[0] 	= Texture;

		Texture[1]		= (c_tShadowMap);

        ColorOp[1]		= SelectArg1;
        ColorArg1[1]	= Current;
        
        AlphaOp[1]		= Modulate;
        AlphaArg1[1]	= Texture;
        AlphaArg2[1]	= Current;

		Texture[2]		= (c_tShadowFadeTexture);

        ColorOp[2]		= SelectArg1;
        ColorArg1[2]	= Current;

        AlphaOp[2] 	 	= Modulate;
        AlphaArg1[2]	= Current;
        AlphaArg2[2]	= Texture;
        
		AddressU[0] 	= Clamp;
		AddressV[0] 	= Clamp;

		AddressU[1] 	= Clamp;
		AddressV[1] 	= Clamp;
		
		AddressU[2] 	= Clamp;
		AddressV[2] 	= Clamp;
	}
}

/**
	trivial case (only one material) with shadow
*/
technique trivial_shadowed
{
    pass p0
    {
        VertexShader = (vsTrivalPassShadowed);
        PixelShader  = 0;

		AlphaTestEnable = false;

		ZEnable 		= true;
		ZWriteEnable 	= true;
        ZFunc           = LessEqual;		

        CullMode		= <c_iCullMode>;

		AlphaBlendEnable = true;
		SrcBlend		 = invsrcalpha;
		DestBlend		 = zero;

		Texture[0]		= (c_tDiffuseMap);

        ColorOp[0] 	 	= Modulate;
        ColorArg1[0] 	= Texture;
        ColorArg2[0] 	= Diffuse;
        
        AlphaOp[0] 	 	= SelectArg1;
        AlphaArg1[0] 	= Texture;

		Texture[1]		= (c_tShadowMap);

        ColorOp[1]		= SelectArg1;
        ColorArg1[1]	= Current;
        
        AlphaOp[1]		= Modulate;
        AlphaArg1[1]	= Texture;
        AlphaArg2[1]	= Current;

		Texture[2]		= (c_tShadowFadeTexture);

        ColorOp[2]		= SelectArg1;
        ColorArg1[2]	= Current;

        AlphaOp[2] 	 	= Modulate;
        AlphaArg1[2]	= Current;
        AlphaArg2[2]	= Texture;
        
		AddressU[0] 	= Wrap;
		AddressV[0] 	= Wrap;

		AddressU[1] 	= Clamp;
		AddressV[1] 	= Clamp;
		
		AddressU[2] 	= Clamp;
		AddressV[2] 	= Clamp;
	}
}

/**
	shadow pass; renders shadows after all other passes
*/
technique shadowpass
{
    pass p0
    {
        VertexShader = (vsShadowPass);
        PixelShader  = 0;

		AlphaTestEnable = false;

		ZEnable 		= true;
		ZWriteEnable 	= true;
        ZFunc           = LessEqual;		

        CullMode  = <c_iCullMode>;

		AlphaBlendEnable = true;
		SrcBlend		 = srcalpha;
		DestBlend		 = invsrcalpha;

		Texture[0]		= (c_tShadowMap);

        ColorOp[0] 	 	= SelectArg1;
        ColorArg1[0] 	= Texture;
        
        AlphaOp[0] 	 	= SelectArg1;
        AlphaArg1[0] 	= Texture;

		Texture[1]		= (c_tShadowFadeTexture);

        ColorOp[1]		= SelectArg1;
        ColorArg1[1]	= Texture;

        AlphaOp[1]		= Modulate;
        AlphaArg1[1]	= Current;
        AlphaArg2[1]	= Texture;
        
        ColorOp[2]		= Disable;
        AlphaOp[2]		= Disable;
        
		AddressU[0] 	= Clamp;
		AddressV[0] 	= Clamp;

		AddressU[1] 	= Clamp;
		AddressV[1] 	= Clamp;
	}
}
