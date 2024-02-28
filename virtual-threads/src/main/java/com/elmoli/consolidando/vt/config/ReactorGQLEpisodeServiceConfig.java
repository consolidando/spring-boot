/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.config;

import com.elmoli.consolidando.vt.client.EpisodeApiWebClient;
import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import com.elmoli.consolidando.vt.service.EpisodeService;
import com.elmoli.consolidando.vt.service.ReactorGQLEpisodeServide;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
@Profile(
        {
            "reactor-gql"
        })
public class ReactorGQLEpisodeServiceConfig
{

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory)
    {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schemaflux.sql")));

        return initializer;
    }

    @Bean
    public EpisodeService episodiApi(EpisodeApiWebClient episodeApiClient,
            CharacterFluxRepository characterFluxRepository)
    {
        return new ReactorGQLEpisodeServide(episodeApiClient, characterFluxRepository);
    }

}
