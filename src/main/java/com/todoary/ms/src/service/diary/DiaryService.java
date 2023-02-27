package com.todoary.ms.src.service.diary;


import com.todoary.ms.src.common.event.DiaryCreatedEvent;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.domain.*;
import com.todoary.ms.src.repository.DiaryRepository;
import com.todoary.ms.src.repository.StickerRepository;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.diary.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.todoary.ms.src.common.response.BaseResponseStatus.INVALID_STICKER_ID;
import static com.todoary.ms.src.common.response.BaseResponseStatus.USERS_DIARY_NOT_EXISTS;
import static com.todoary.ms.src.web.dto.diary.StickersRequest.StickerDeleteRequest;
import static com.todoary.ms.src.web.dto.diary.StickersRequest.StickerUpdateRequest;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final StickerRepository stickerRepository;
    private final MemberService memberService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void saveDiaryOrUpdate(Long memberId, DiaryRequest request, LocalDate createdDate) {
        Member member = memberService.findById(memberId);
        findDiaryByDateIfExists(createdDate, member)
                .ifPresentOrElse(
                        diary -> diary.update(request.getTitle(), request.getContent()),
                        () -> saveDiary(Diary.of(member, request.getTitle(), request.getContent(), createdDate))
                );
    }

    private void saveDiary(Diary diary) {
        diaryRepository.save(diary);
        // 일기 생성 시 remind alarm 테이블 업데이트해야하므로 이벤트발생
        publisher.publishEvent(new DiaryCreatedEvent(diary.getMember().getId(), diary.getCreatedDate()));
    }

    @Transactional
    public void deleteDiary(Long memberId, LocalDate createdDate) {
        Member member = memberService.findById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        diary.removeAssociations();
        diaryRepository.delete(diary);
    }

    @Transactional(readOnly = true)
    public DiaryResponse findDiaryByDate(LocalDate createdDate, Long memberId) {
        Member member = memberService.findById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        return DiaryResponse.from(diary);
    }

    @Transactional(readOnly = true)
    public List<Integer> findDaysHavingDiaryInMonth(Long memberId, YearMonth yearMonth) {
        Member member = memberService.findById(memberId);

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        return diaryRepository.findBetweenDaysAndMember(firstDay, lastDay, member)
                .stream().map(diary -> diary.getCreatedDate().getDayOfMonth())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Diary> findDiaryByDateIfExists(LocalDate createdDate, Member member) {
        return diaryRepository.findByMemberAndDate(member, createdDate);
    }

    @Transactional(readOnly = true)
    public Diary findDiaryByDate(LocalDate createdDate, Member member) {
        return diaryRepository.findByMemberAndDate(member, createdDate)
                .orElseThrow(() -> new TodoaryException(USERS_DIARY_NOT_EXISTS));
    }

    @Transactional(readOnly = true)
    public List<StickerResponse> findStickersInDiaryOnDate(Long memberId, LocalDate createdDate) {
        Member member = memberService.findById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        return diary.getStickers()
                .stream().map(StickerResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public List<Long> updateStickersInDiaryAndGetCreated(Long memberId, LocalDate createdDate, StickersRequest request) {
        // update 후 삭제하는 일이 없게 하기 위해 delete를 먼저 함
        // (삭제 후 같은 아이디에 대해 업데이트하면 롤백됨)
        deleteStickers(request.getDeleted());
        updateStickers(request.getModified());
        // save 먼저할 경우 insert 후 롤백되는 과정에서 id가 증가하기 때문에 나중에 함
        return saveStickersInDiaryOnDate(memberId, createdDate, request.getCreated());
    }

    @Transactional
    public List<Long> saveStickersInDiaryOnDate(Long memberId, LocalDate createdDate, List<StickerRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return null;
        }
        Member member = memberService.findById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        return stickerRepository.saveAll(
                        requests.stream()
                                .filter(Objects::nonNull)
                                .map(req -> req.toEntity(diary))
                                .collect(Collectors.toList())
                ).stream()
                .map(Sticker::getId).collect(Collectors.toList());
    }

    private void updateStickers(List<StickerUpdateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }
        requests.forEach(
                req -> findSticker(req.getId())
                        .update(StickerType.from(req.getStickerId()), StickerShape.from(req))
        );
    }

    private void deleteStickers(List<StickerDeleteRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }
        requests.forEach(
                req -> deleteSticker(req.getId())
        );
    }

    private void deleteSticker(Long id) {
        Sticker sticker = findSticker(id);
        sticker.removeAssociation();
        stickerRepository.delete(sticker);
    }

    private Sticker findSticker(Long id) {
        return stickerRepository.findById(id)
                .orElseThrow(() -> new TodoaryException(INVALID_STICKER_ID));
    }
}
