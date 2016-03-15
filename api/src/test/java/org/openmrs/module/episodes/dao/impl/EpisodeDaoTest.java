package org.openmrs.module.episodes.dao.impl;

import org.junit.Test;
import org.openmrs.PatientProgram;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.episodes.Episode;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class EpisodeDaoTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EpisodeDAO episodeDAO;
    @Autowired
    private EncounterService encounterService;
    @Autowired
    private ProgramWorkflowService programWorkflowService;

    @Test
    public void shouldCreateANewEpisode() {
        Episode episode = new Episode();
        episodeDAO.save(episode);
        assertThat(episode.getId(), is(notNullValue()));
        Episode savedEpisode = episodeDAO.get(episode.getId());
        assertThat(savedEpisode.getEncounters(), is(notNullValue()));
    }

    @Test
    public void shouldCreateANewEpisodeWithEncounter() {
        Episode episode = new Episode();
        episode.addEncounter(encounterService.getEncounter(3));
        episode.addEncounter(encounterService.getEncounter(4));
        episode.addEncounter(encounterService.getEncounter(5));
        episodeDAO.save(episode);

        Episode savedEpisode = episodeDAO.get(episode.getId());
        assertThat(savedEpisode.getEncounters().size(), is(3));
    }

    @Test
    public void shouldCreateANewEpisodeWithEncounterAndPatientProgram() {
        Episode episode = new Episode();
        episode.addEncounter(encounterService.getEncounter(3));
        episode.addEncounter(encounterService.getEncounter(4));
        episode.addEncounter(encounterService.getEncounter(5));

        episode.addPatientProgram(programWorkflowService.getPatientProgram(1));
        episodeDAO.save(episode);

        Episode savedEpisode = episodeDAO.get(episode.getId());
        assertThat(savedEpisode.getPatientPrograms().size(), is(1));
    }

    @Test
    public void shouldRetrieveEpisodeForAProgram() {
        Episode episode = new Episode();
        episode.addPatientProgram(programWorkflowService.getPatientProgram(1));
        episodeDAO.save(episode);
        PatientProgram patientProgram = programWorkflowService.getPatientProgram(1);

        Episode episodeForPatientProgram = episodeDAO.getEpisodeForPatientProgram(patientProgram);

        Set<PatientProgram> patientPrograms = episodeForPatientProgram.getPatientPrograms();
        assertThat(patientPrograms.size(), is(equalTo(1)));
        assertThat(patientPrograms.iterator().next().getUuid(), is(equalTo(patientProgram.getUuid())));
    }

    @Test
    public void shouldReturnNullIfEpisodeNotFoundForProgram() {
        PatientProgram patientProgram = programWorkflowService.getPatientProgram(1);
        assertThat(patientProgram, is(notNullValue()));

        Episode episodeForPatientProgram = episodeDAO.getEpisodeForPatientProgram(patientProgram);

        assertThat(episodeForPatientProgram, is(nullValue()));
    }

    @Test (expected = Exception.class)
    public void shouldThrowExceptionIfTransientProgramInstanceUsedToRetrieveEpisode() {
        episodeDAO.getEpisodeForPatientProgram(new PatientProgram());
    }

    @Test
    public void shouldReturnNullIfProgramToFetchEpisodeIsNull() {
        Episode episodeForPatientProgram = episodeDAO.getEpisodeForPatientProgram(null);

        assertThat(episodeForPatientProgram, is(nullValue()));
    }
}
