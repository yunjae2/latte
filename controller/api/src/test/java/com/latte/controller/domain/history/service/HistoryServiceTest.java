package com.latte.controller.domain.history.service;

import com.latte.controller.domain.TestHistory;
import com.latte.controller.domain.history.infrastructure.HistoryRepository;
import com.latte.controller.domain.history.interfaces.request.HistorySearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class HistoryServiceTest {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private HistoryRepository historyRepository;

    @BeforeEach
    void beforeEach() {
        historyRepository.deleteAll();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    void fetch_test(String name, List<TestHistory> testHistories, HistorySearchRequest historySearchRequest, List<TestHistory> expected) {
        // given
        historyRepository.saveAllAndFlush(testHistories);

        // when
        List<TestHistory> actual = historyService.fetch(historySearchRequest).block();

        // then
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private static Stream<Arguments> fetch_test() {
        return Stream.of(
                Arguments.of("Pagination: first page",
                        List.of(
                                buildHistory("1", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("2", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("3", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        ),
                        HistorySearchRequest.builder()
                                .page(0)
                                .size(2)
                                .order(SortOrder.ASC)
                                .orderBy("name")
                                .build(),
                        List.of(
                                buildHistory("1", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("2", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        )
                ),
                Arguments.of("Pagination: second page",
                        List.of(
                                buildHistory("1", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("2", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("3", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        ),
                        HistorySearchRequest.builder()
                                .page(1)
                                .size(2)
                                .order(SortOrder.ASC)
                                .orderBy("name")
                                .build(),
                        List.of(
                                buildHistory("3", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        )
                ),
                Arguments.of("Sort: order by actualTps",
                        List.of(
                                buildHistory("1", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("2", 4.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("3", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        ),
                        HistorySearchRequest.builder()
                                .page(0)
                                .size(2)
                                .order(SortOrder.ASC)
                                .orderBy("actualTps")
                                .build(),
                        List.of(
                                buildHistory("3", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("1", 3.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        )
                ),
                Arguments.of("Sort: order by date",
                        List.of(
                                buildHistory("1", 2.5, LocalDateTime.of(2022, 3, 8, 2, 10)),
                                buildHistory("2", 2.5, LocalDateTime.of(2022, 3, 8, 2, 30)),
                                buildHistory("3", 2.5, LocalDateTime.of(2022, 3, 8, 2, 20))
                        ),
                        HistorySearchRequest.builder()
                                .page(0)
                                .size(2)
                                .order(SortOrder.ASC)
                                .orderBy("date")
                                .build(),
                        List.of(
                                buildHistory("1", 2.5, LocalDateTime.of(2022, 3, 8, 2, 10)),
                                buildHistory("3", 2.5, LocalDateTime.of(2022, 3, 8, 2, 20))
                        )
                ),
                Arguments.of("Sort: ascending order",
                        List.of(
                                buildHistory("20", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("10", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("30", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        ),
                        HistorySearchRequest.builder()
                                .page(0)
                                .size(2)
                                .order(SortOrder.ASC)
                                .orderBy("name")
                                .build(),
                        List.of(
                                buildHistory("10", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("20", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        )
                ),
                Arguments.of("Sort: descending order",
                        List.of(
                                buildHistory("20", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("10", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("30", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        ),
                        HistorySearchRequest.builder()
                                .page(0)
                                .size(2)
                                .order(SortOrder.DESC)
                                .orderBy("name")
                                .build(),
                        List.of(
                                buildHistory("30", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0)),
                                buildHistory("20", 2.5, LocalDateTime.of(2022, 3, 8, 2, 0))
                        )
                )
        );
    }

    private static TestHistory buildHistory(String name, Double tps, LocalDateTime date) {
        return TestHistory.builder()
                .name(name)
                .actualTps(tps)
                .date(date)
                .build();
    }

}