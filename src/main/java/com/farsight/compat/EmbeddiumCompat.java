package com.farsight.compat;

import com.farsight.FarsightClientChunkManager;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkStatus;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;

public class EmbeddiumCompat
{
    public static void init()
    {
        FarsightClientChunkManager.loadCallback.add(((clientLevel, levelChunk) -> ChunkTrackerHolder.get(clientLevel)
          .onChunkStatusAdded(levelChunk.getPos().x, levelChunk.getPos().z, ChunkStatus.FLAG_HAS_BLOCK_DATA)));
        FarsightClientChunkManager.unloadCallback.add(((clientLevel, levelChunk) -> ChunkTrackerHolder.get(clientLevel)
          .onChunkStatusRemoved(levelChunk.getPos().x, levelChunk.getPos().z, ChunkStatus.FLAG_HAS_BLOCK_DATA)));
    }
}
