package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;

import java.lang.annotation.Annotation;
import java.util.List;

public record ClanServicePartFactory<T extends Model & DocumentCodec>
        (ClanService<T> clanService)
        implements PartFactory {

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list) {
        return new ClanServicePart<>(name, clanService);
    }
}
