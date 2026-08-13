"use client";

import React, { useState, useEffect } from "react";
import { 
  Sparkles, 
  Film, 
  Image as ImageIcon, 
  CheckCircle2, 
  RefreshCw, 
  Play, 
  Download, 
  Bot, 
  UserCheck, 
  Wand2, 
  ChevronRight,
  AlertTriangle,
  Radio,
  FileVideo,
  ListVideo
} from "lucide-react";

interface Idea {
  id: number;
  title: string;
  concept: string;
  cuts: { order: number; description: string }[];
}

interface PromptHistory {
  id: number;
  version: number;
  content: string;
  negativeContent: string;
  isApproved: boolean;
}

interface CutState {
  order: number;
  description: string;
  imagePrompt: PromptHistory | null;
  imageFeedback: string;
  isImageLoading: boolean;
  isImageApproved: boolean;
  videoPrompt: PromptHistory | null;
  videoFeedback: string;
  isVideoLoading: boolean;
  isVideoApproved: boolean;
  sseStatus: "IDLE" | "CONNECTING" | "GENERATING" | "SUCCESS" | "FAILED";
  videoProgress: number;
  videoUrl: string | null;
}

export default function Dashboard() {
  const [currentStep, setCurrentStep] = useState<number>(1);
  const [topic, setTopic] = useState<string>("러시 아워를 맞아 8개의 다리로 화려하게 커피를 만드는 스타 바리스타 문어");
  const [isGeneratingIdeas, setIsGeneratingIdeas] = useState<boolean>(false);
  const [ideas, setIdeas] = useState<Idea[]>([]);
  const [selectedIdea, setSelectedIdea] = useState<Idea | null>(null);

  const [projectId, setProjectId] = useState<number | null>(null);
  const [cuts, setCuts] = useState<CutState[]>([]);
  const [activeCutOrder, setActiveCutOrder] = useState<number>(1);

  const handleGenerateIdeas = async () => {
    setIsGeneratingIdeas(true);
    // Simulating backend call for 4-cut storyboard
    setTimeout(() => {
      setIdeas([
        {
          id: 1,
          title: "스타 바리스타 문어의 러시 아워 대환장 파티",
          concept: "8개의 다리로 화려하게 커피를 만드는 문어 바리스타. 1인칭 손님 시점.",
          cuts: [
            { order: 1, description: "8개의 다리로 컵 홀더 씌우기, 에스프레소 샷 뽑기, 우유 스팀하기를 동시에 해내는 우아한 모습." },
            { order: 2, description: "주문 기계에서 영수증이 영구차처럼 끝없이 뿜어져 나옴. 문어의 눈동자가 사시가 되며 땀을 뻘뻘 흘림." },
            { order: 3, description: "극도의 스트레스를 받은 문어가 다리를 꼬아 샷 잔을 떨어뜨리고 먹물을 분출함." },
            { order: 4, description: "먹물을 뒤집어쓴 손님(카메라)과 당황하여 다리로 머리를 감싸는 문어." }
          ]
        },
        {
          id: 2,
          title: "항공기 기장 펭귄의 연어 참기 미션",
          concept: "기장 제복을 입은 펭귄 기장이 조종실에서 근무하는 모습.",
          cuts: [
            { order: 1, description: "기장 제복을 입은 펭귄이 진지한 표정으로 조종간을 잡고 시청자를 돌아봄." },
            { order: 2, description: "스튜어디스가 기내식으로 연어를 가져옴. 펭귄 기장의 동공이 흔들림." },
            { order: 3, description: "본능을 참지 못하고 부리로 연어를 낚아채며 난동을 부림." },
            { order: 4, description: "비행기가 흔들리고 조종실 전체가 연어 파티로 난장판이 됨." }
          ]
        }
      ]);
      setIsGeneratingIdeas(false);
    }, 1500);
  };

  const handleSelectIdea = (idea: Idea) => {
    setSelectedIdea(idea);
    setProjectId(Date.now());
    
    // Initialize cuts
    const initialCuts: CutState[] = idea.cuts.map(c => ({
      order: c.order,
      description: c.description,
      imagePrompt: null,
      imageFeedback: "",
      isImageLoading: false,
      isImageApproved: false,
      videoPrompt: null,
      videoFeedback: "",
      isVideoLoading: false,
      isVideoApproved: false,
      sseStatus: "IDLE",
      videoProgress: 0,
      videoUrl: null
    }));
    setCuts(initialCuts);
    setCurrentStep(2);
    setActiveCutOrder(1);
    
    // Auto-generate prompts for all cuts initially
    initialCuts.forEach(c => handleGenerateImagePrompt(c.order, c.description));
  };

  const updateCut = (order: number, updates: Partial<CutState>) => {
    setCuts(prev => prev.map(c => c.order === order ? { ...c, ...updates } : c));
  };

  const activeCut = cuts.find(c => c.order === activeCutOrder);

  // STEP 2: Image Prompt Generation
  const handleGenerateImagePrompt = (order: number, description: string) => {
    updateCut(order, { isImageLoading: true });
    setTimeout(() => {
      updateCut(order, {
        imagePrompt: {
          id: Date.now() + order,
          version: 1,
          content: `First-person POV. ${description} 3D animation style, Pixar style, highly detailed, cinematic warm lighting. --ar 9:16`,
          negativeContent: `No text, no clutter, high quality`,
          isApproved: false
        },
        isImageLoading: false
      });
    }, 1500 + order * 500);
  };

  const handleImageFeedback = (order: number) => {
    const cut = cuts.find(c => c.order === order);
    if (!cut || !cut.imagePrompt || !cut.imageFeedback) return;
    
    updateCut(order, { isImageLoading: true });
    setTimeout(() => {
      updateCut(order, {
        imagePrompt: {
          ...cut.imagePrompt!,
          version: cut.imagePrompt!.version + 1,
          content: cut.imagePrompt!.content + ` [Refined: ${cut.imageFeedback}]`,
        },
        imageFeedback: "",
        isImageLoading: false
      });
    }, 1200);
  };

  const handleApproveImagePrompt = (order: number) => {
    updateCut(order, { isImageApproved: true });
    
    // Check if ALL cuts are approved to move to Step 3
    const allApproved = cuts.map(c => c.order === order ? true : c.isImageApproved).every(Boolean);
    if (allApproved) {
      setCurrentStep(3);
      setActiveCutOrder(1);
      cuts.forEach(c => handleGenerateVideoPrompt(c.order));
    } else {
      // Move to next unapproved cut
      const nextCut = cuts.find(c => c.order !== order && !c.isImageApproved);
      if (nextCut) setActiveCutOrder(nextCut.order);
    }
  };

  // STEP 3: Video Prompt Generation
  const handleGenerateVideoPrompt = (order: number) => {
    updateCut(order, { isVideoLoading: true });
    setTimeout(() => {
      updateCut(order, {
        videoPrompt: {
          id: Date.now() + 1000 + order,
          version: 1,
          content: `Dynamic action! Cut ${order}. CRITICAL WARNING: No sudden light flashes. IMMEDIATELY a big effect happens.`,
          negativeContent: `No sudden light flashes`,
          isApproved: false
        },
        isVideoLoading: false
      });
    }, 1500 + order * 500);
  };

  const handleVideoFeedback = (order: number) => {
    const cut = cuts.find(c => c.order === order);
    if (!cut || !cut.videoPrompt || !cut.videoFeedback) return;
    
    updateCut(order, { isVideoLoading: true });
    setTimeout(() => {
      updateCut(order, {
        videoPrompt: {
          ...cut.videoPrompt!,
          version: cut.videoPrompt!.version + 1,
          content: cut.videoPrompt!.content + ` [Refined Motion: ${cut.videoFeedback}]`,
        },
        videoFeedback: "",
        isVideoLoading: false
      });
    }, 1200);
  };

  const handleTriggerVideoGeneration = (order: number) => {
    updateCut(order, { isVideoApproved: true, sseStatus: "CONNECTING" });
    
    setTimeout(() => {
      updateCut(order, { sseStatus: "GENERATING" });
      let progress = 0;
      const interval = setInterval(() => {
        progress += 20;
        updateCut(order, { videoProgress: Math.min(progress, 100) });
        if (progress >= 100) {
          clearInterval(interval);
          updateCut(order, { sseStatus: "SUCCESS", videoUrl: `/sample_cut_${order}.mp4` });
          
          // Check if all are success
          setCuts(prev => {
            const allSuccess = prev.every(c => c.sseStatus === "SUCCESS");
            if (allSuccess) {
               // You can trigger step 4 here if you want
            }
            return prev;
          });
        }
      }, 1000);
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-[#0b0f19] text-gray-100 p-6 md:p-10 font-sans">
      {/* Top Header */}
      <header className="max-w-6xl mx-auto flex items-center justify-between pb-8 border-b border-gray-800">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-gradient-to-tr from-purple-600 to-indigo-500 rounded-2xl glow-purple">
            <Film className="w-7 h-7 text-white" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight bg-gradient-to-r from-purple-400 via-indigo-300 to-white bg-clip-text text-transparent">
              WorkFluffs Shorts AI Studio
            </h1>
            <p className="text-xs text-gray-400">@WorkFluffs 3D Animation Shorts Multi-Agent Pipeline</p>
          </div>
        </div>
      </header>

      {/* Workflow Stepper Bar */}
      <div className="max-w-6xl mx-auto my-8">
        <div className="grid grid-cols-4 gap-4">
          {[
            { step: 1, label: "소재 기획", icon: Wand2 },
            { step: 2, label: "이미지 프롬프트 (컷별)", icon: ImageIcon },
            { step: 3, label: "모션 프롬프트 (컷별)", icon: Film },
            { step: 4, label: "영상 병합", icon: Play }
          ].map((s) => {
            const Icon = s.icon;
            const isActive = currentStep === s.step;
            const isDone = currentStep > s.step;
            return (
              <div 
                key={s.step}
                className={`p-4 rounded-xl border flex items-center gap-3 transition-all ${
                  isActive 
                    ? "bg-purple-950/40 border-purple-500 text-purple-200 glow-purple"
                    : isDone
                    ? "bg-gray-900/60 border-green-500/40 text-green-400"
                    : "bg-gray-900/40 border-gray-800 text-gray-500"
                }`}
              >
                <div className={`p-2 rounded-lg ${isActive ? "bg-purple-600 text-white" : isDone ? "bg-green-600/20 text-green-400" : "bg-gray-800 text-gray-400"}`}>
                  <Icon className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-[10px] uppercase font-semibold text-gray-400">Phase {s.step}</div>
                  <div className="text-sm font-medium">{s.label}</div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <main className="max-w-6xl mx-auto space-y-6">
        {/* Step 1: Idea Generation */}
        {currentStep === 1 && (
          <div className="glass-panel p-8 rounded-2xl border border-gray-800 space-y-6">
            <h2 className="text-xl font-bold text-white flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-purple-400" />
              Step 1: 멀티 컷 스토리보드 도출
            </h2>
            <div className="flex gap-3">
              <input
                type="text"
                value={topic}
                onChange={(e) => setTopic(e.target.value)}
                className="flex-1 bg-gray-950 border border-gray-800 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-purple-500"
              />
              <button
                onClick={handleGenerateIdeas}
                disabled={isGeneratingIdeas}
                className="bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white font-medium px-6 py-3 rounded-xl flex items-center gap-2 text-sm transition-all"
              >
                {isGeneratingIdeas ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Wand2 className="w-4 h-4" />}
                <span>스토리보드 도출</span>
              </button>
            </div>

            {ideas.length > 0 && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4">
                {ideas.map((idea) => (
                  <div key={idea.id} className="glass-card p-5 rounded-xl border border-gray-800 hover:border-purple-500/50 transition-all flex flex-col justify-between space-y-4">
                    <div>
                      <h3 className="font-bold text-white text-base">{idea.title}</h3>
                      <p className="text-xs text-purple-400 mt-1">{idea.concept}</p>
                    </div>
                    <div className="space-y-2">
                      {idea.cuts.map(cut => (
                        <div key={cut.order} className="text-xs text-gray-300 bg-gray-900/50 p-2 rounded">
                          <span className="font-bold text-white mr-2">컷 {cut.order}:</span>
                          {cut.description}
                        </div>
                      ))}
                    </div>
                    <button
                      onClick={() => handleSelectIdea(idea)}
                      className="w-full py-2.5 bg-purple-600/20 hover:bg-purple-600 text-purple-300 hover:text-white rounded-lg text-xs font-semibold flex items-center justify-center gap-1.5 transition-all"
                    >
                      이 스토리보드 선택
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Step 2 & 3: Cut Timeline Split View */}
        {(currentStep === 2 || currentStep === 3) && selectedIdea && (
          <div className="grid grid-cols-12 gap-6">
            
            {/* Timeline Sidebar */}
            <div className="col-span-3 space-y-3">
              <div className="bg-gray-900/80 p-4 rounded-xl border border-gray-800">
                <h3 className="text-sm font-bold text-white mb-4 flex items-center gap-2">
                  <ListVideo className="w-4 h-4 text-purple-400" />
                  스토리보드 타임라인
                </h3>
                <div className="space-y-2">
                  {cuts.map(cut => {
                    const isActive = cut.order === activeCutOrder;
                    const isApproved = currentStep === 2 ? cut.isImageApproved : cut.isVideoApproved;
                    return (
                      <button
                        key={cut.order}
                        onClick={() => setActiveCutOrder(cut.order)}
                        className={`w-full text-left p-3 rounded-lg border transition-all ${
                          isActive ? "bg-purple-900/30 border-purple-500" : "bg-gray-950 border-gray-800 hover:border-gray-600"
                        }`}
                      >
                        <div className="flex justify-between items-center">
                          <span className={`text-xs font-bold ${isActive ? "text-purple-300" : "text-gray-400"}`}>
                            Cut {cut.order}
                          </span>
                          {isApproved && <CheckCircle2 className="w-3.5 h-3.5 text-green-500" />}
                        </div>
                        <p className="text-[10px] text-gray-500 mt-1 line-clamp-2">{cut.description}</p>
                      </button>
                    )
                  })}
                </div>
              </div>
            </div>

            {/* Cut Detail Main Area */}
            <div className="col-span-9 glass-panel p-6 rounded-2xl border border-gray-800">
              <div className="mb-6 border-b border-gray-800 pb-4 flex justify-between items-center">
                <div>
                  <h2 className="text-xl font-bold text-white">Cut {activeCutOrder} 상세 에디터</h2>
                  <p className="text-sm text-gray-400 mt-1">{activeCut?.description}</p>
                </div>
                {currentStep === 2 ? (
                  <span className="px-3 py-1 bg-indigo-900/60 text-indigo-300 text-xs rounded-full border border-indigo-700/60">이미지 피드백 단계</span>
                ) : (
                  <span className="px-3 py-1 bg-blue-900/60 text-blue-300 text-xs rounded-full border border-blue-700/60">비디오 모션 피드백 단계</span>
                )}
              </div>

              {/* Step 2 Content */}
              {currentStep === 2 && activeCut && (
                <div className="space-y-6">
                  {activeCut.isImageLoading ? (
                    <div className="py-12 text-center text-purple-400 animate-pulse text-sm">프롬프트 처리 중...</div>
                  ) : activeCut.imagePrompt ? (
                    <div className="space-y-4">
                      <div className="bg-gray-950 p-4 rounded-xl border border-gray-800">
                        <div className="text-xs font-bold text-green-400 mb-2">Image Prompt (Version {activeCut.imagePrompt.version})</div>
                        <p className="text-sm text-gray-300 font-mono">{activeCut.imagePrompt.content}</p>
                      </div>
                      
                      {!activeCut.isImageApproved && (
                        <div className="flex gap-2">
                          <input
                            type="text"
                            value={activeCut.imageFeedback}
                            onChange={(e) => updateCut(activeCut.order, { imageFeedback: e.target.value })}
                            placeholder="이미지 수정 요청..."
                            className="flex-1 bg-gray-900 border border-gray-800 rounded-lg px-3 py-2 text-xs text-white"
                          />
                          <button
                            onClick={() => handleImageFeedback(activeCut.order)}
                            className="bg-purple-900 hover:bg-purple-800 text-purple-200 px-4 py-2 rounded-lg text-xs"
                          >
                            피드백 반영
                          </button>
                          <button
                            onClick={() => handleApproveImagePrompt(activeCut.order)}
                            className="bg-green-600 hover:bg-green-500 text-white px-4 py-2 rounded-lg text-xs font-bold flex items-center gap-1"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5"/> 승인
                          </button>
                        </div>
                      )}
                      {activeCut.isImageApproved && (
                        <div className="text-sm text-green-400 font-semibold flex items-center gap-2">
                          <CheckCircle2 className="w-4 h-4"/> 이 컷의 이미지가 승인되었습니다.
                        </div>
                      )}
                    </div>
                  ) : null}
                </div>
              )}

              {/* Step 3 Content */}
              {currentStep === 3 && activeCut && (
                <div className="space-y-6">
                  {activeCut.isVideoLoading ? (
                    <div className="py-12 text-center text-blue-400 animate-pulse text-sm">비디오 프롬프트 생성 중...</div>
                  ) : activeCut.videoPrompt ? (
                    <div className="space-y-4">
                      <div className="bg-gray-950 p-4 rounded-xl border border-gray-800">
                        <div className="text-xs font-bold text-blue-400 mb-2">Video Motion Prompt (Version {activeCut.videoPrompt.version})</div>
                        <p className="text-sm text-gray-300 font-mono">{activeCut.videoPrompt.content}</p>
                      </div>
                      
                      {!activeCut.isVideoApproved ? (
                        <div className="flex gap-2">
                          <input
                            type="text"
                            value={activeCut.videoFeedback}
                            onChange={(e) => updateCut(activeCut.order, { videoFeedback: e.target.value })}
                            placeholder="모션 수정 요청..."
                            className="flex-1 bg-gray-900 border border-gray-800 rounded-lg px-3 py-2 text-xs text-white"
                          />
                          <button
                            onClick={() => handleVideoFeedback(activeCut.order)}
                            className="bg-blue-900 hover:bg-blue-800 text-blue-200 px-4 py-2 rounded-lg text-xs"
                          >
                            피드백 반영
                          </button>
                          <button
                            onClick={() => handleTriggerVideoGeneration(activeCut.order)}
                            className="bg-green-600 hover:bg-green-500 text-white px-4 py-2 rounded-lg text-xs font-bold flex items-center gap-1"
                          >
                            <Play className="w-3.5 h-3.5"/> 영상 생성 (Approve)
                          </button>
                        </div>
                      ) : (
                        <div className="bg-gray-900/50 p-4 rounded-xl border border-gray-800 space-y-3">
                          <div className="flex justify-between text-xs text-gray-400">
                            <span>Status: {activeCut.sseStatus}</span>
                            <span>{activeCut.videoProgress}%</span>
                          </div>
                          <div className="w-full h-2 bg-gray-950 rounded-full overflow-hidden">
                            <div className="h-full bg-gradient-to-r from-blue-500 to-purple-400 transition-all" style={{ width: `${activeCut.videoProgress}%` }} />
                          </div>
                          {activeCut.sseStatus === "SUCCESS" && (
                            <div className="text-xs text-green-400 font-semibold">비디오 생성 완료!</div>
                          )}
                        </div>
                      )}
                    </div>
                  ) : null}
                </div>
              )}
            </div>

          </div>
        )}
      </main>
    </div>
  );
}
