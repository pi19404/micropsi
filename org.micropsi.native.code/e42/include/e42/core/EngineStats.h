#pragma once

#ifndef E42_ENGINESTATS_H_INCLUDED
#define E42_ENGINESTATS_H_INCLUDED

class CEngineStats
{
public:
    CEngineStats();
    ~CEngineStats();

    int m_iRenderedPolys;
    int m_iRenderedMeshes;
    int m_iRenderedVertexGroups;
    int m_iNumTextureSwitches;

    void NextFrame();
};

#endif // E42_ENGINESTATS_H_INCLUDED